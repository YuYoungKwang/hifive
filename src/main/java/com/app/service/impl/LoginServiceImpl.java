package com.app.service.impl;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.app.dao.AuthDAO;
import com.app.dto.UsersDTO;
import com.app.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

    private static final String ACTIVE_STATUS = "A";
    private static final String DEFAULT_USER_TYPE = "LOCAL";
    private static final String DEFAULT_SNS_TYPE = "LOCAL";
    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private final AuthDAO authDAO;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public LoginServiceImpl(AuthDAO authDAO, PasswordEncoder passwordEncoder) {
        this.authDAO = authDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UsersDTO register(String userId,
                            String email,
                            String rawPassword,
                            String userName,
                            String userNickName,
                            String userGender,
                            String userPhoneNumber,
                            String userRegistrationNo,
                            String userProfileImg,
                            String userZipCode,
                            String userBaseAddress,
                            String userDetailAddress) {
        if (!StringUtils.hasText(userId)
                || !StringUtils.hasText(email)
                || !StringUtils.hasText(rawPassword)
                || !StringUtils.hasText(userName)
                || !StringUtils.hasText(userNickName)
                || !StringUtils.hasText(userPhoneNumber)
                || !StringUtils.hasText(userRegistrationNo)) {
            throw new IllegalArgumentException(
                    "필수 입력값(아이디, 이메일, 비밀번호, 이름, 닉네임, 연락처, 생년월일)을 모두 입력해 주세요.");
        }

        String normalizedUserId = userId.trim();
        String normalizedEmail = email.trim().toLowerCase();

        if (authDAO.existsAuthId(normalizedUserId)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (authDAO.existsEmail(normalizedEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String resolvedUserName = userName.trim();
        String resolvedNickName = userNickName.trim();
        String resolvedGender = normalizeGender(userGender);

        Long userNo = authDAO.nextUserNo();
        Long userAuthNo = authDAO.nextUserAuthNo();

        UsersDTO newUser = new UsersDTO();
        newUser.setUserNo(userNo);
        newUser.setUserType(DEFAULT_USER_TYPE);
        newUser.setUserStatus(ACTIVE_STATUS);
        newUser.setUserName(resolvedUserName);
        newUser.setUserGender(resolvedGender);
        newUser.setUserPhoneNumber(normalizeBlank(userPhoneNumber));
        newUser.setUserRegistrationNo(normalizeBlank(userRegistrationNo));
        newUser.setUserNickName(resolvedNickName);
        newUser.setUserProfileImg(normalizeBlank(userProfileImg));
        newUser.setUserZipCode(normalizeBlank(userZipCode));
        newUser.setUserBaseAddress(normalizeBlank(userBaseAddress));
        newUser.setUserDetailAddress(normalizeBlank(userDetailAddress));
        newUser.setAuthId(normalizedUserId);
        newUser.setAuthEmail(normalizedEmail);
        newUser.setAuthSnsType(DEFAULT_SNS_TYPE);

        String encodedPassword = passwordEncoder.encode(rawPassword.trim());

        int insertedUsers = authDAO.insertUser(newUser);
        int insertedAuth = authDAO.insertUserAuthentication(newUser, userAuthNo, encodedPassword);
        int insertedAddress = 1;

        if (hasAnyAddress(newUser)) {
            Long userAddressNo = authDAO.nextUserAddressNo();
            newUser.setUserAddressNo(userAddressNo);
            insertedAddress = authDAO.insertUserAddress(newUser);
        }

        if (insertedUsers != 1 || insertedAuth != 1 || insertedAddress != 1) {
            throw new IllegalStateException("회원가입 저장에 실패했습니다.");
        }

        return authDAO.findByAuthId(normalizedUserId);
    }

    @Override
    public String findUserIdByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return authDAO.findAuthIdByEmail(email.trim().toLowerCase());
    }

    @Override
    public String resetPassword(String userId, String email) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(email)) {
            return null;
        }

        String normalizedUserId = userId.trim();
        String normalizedEmail = email.trim().toLowerCase();
        UsersDTO matchedUser = authDAO.findByAuthId(normalizedUserId);

        if (matchedUser == null || !ACTIVE_STATUS.equalsIgnoreCase(matchedUser.getUserStatus())) {
            return null;
        }
        if (!normalizedEmail.equalsIgnoreCase(matchedUser.getAuthEmail())) {
            return null;
        }

        String temporaryPassword = generateTemporaryPassword(10);
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        int updatedRows = authDAO.updatePasswordByAuthIdAndEmail(normalizedUserId, normalizedEmail, encodedPassword);
        if (updatedRows != 1) {
            return null;
        }

        return temporaryPassword;
    }

    @Override
    public UsersDTO findByAuthId(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        return authDAO.findByAuthId(userId.trim());
    }

    @Override
    public boolean isSocialUserMissingAdditionalInfo(String userId) {
        UsersDTO user = findByAuthId(userId);
        if (user == null) {
            return false;
        }
        if (!ACTIVE_STATUS.equalsIgnoreCase(user.getUserStatus())) {
            return false;
        }

        String snsType = user.getAuthSnsType();
        boolean isSocial = StringUtils.hasText(snsType) && !DEFAULT_SNS_TYPE.equalsIgnoreCase(snsType.trim());
        if (!isSocial) {
            return false;
        }

        return !StringUtils.hasText(user.getUserPhoneNumber())
                || !StringUtils.hasText(user.getUserRegistrationNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSocialAdditionalInfo(String userId, String userPhoneNumber, String userRegistrationNo) {
        if (!StringUtils.hasText(userId)
                || !StringUtils.hasText(userPhoneNumber)
                || !StringUtils.hasText(userRegistrationNo)) {
            throw new IllegalArgumentException("연락처와 생년월일은 필수 입력입니다.");
        }

        UsersDTO user = findByAuthId(userId.trim());
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
        }
        if (!ACTIVE_STATUS.equalsIgnoreCase(user.getUserStatus())) {
            throw new IllegalArgumentException("비활성 계정은 수정할 수 없습니다.");
        }

        String snsType = user.getAuthSnsType();
        boolean isSocial = StringUtils.hasText(snsType) && !DEFAULT_SNS_TYPE.equalsIgnoreCase(snsType.trim());
        if (!isSocial) {
            throw new IllegalArgumentException("소셜 로그인 계정만 추가 정보를 입력할 수 있습니다.");
        }

        String normalizedPhone = userPhoneNumber.trim();
        String normalizedRegistrationNo = userRegistrationNo.trim();
        int updatedRows = authDAO.updateSocialAdditionalInfoByAuthId(
                user.getAuthId(),
                normalizedPhone,
                normalizedRegistrationNo);
        if (updatedRows != 1) {
            throw new IllegalStateException("추가 정보 저장에 실패했습니다.");
        }
    }

    private String generateTemporaryPassword(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(TEMP_PASSWORD_CHARS.length());
            builder.append(TEMP_PASSWORD_CHARS.charAt(idx));
        }
        return builder.toString();
    }

    private String normalizeBlank(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeGender(String userGender) {
        if (!StringUtils.hasText(userGender)) {
            return null;
        }
        String value = userGender.trim().toUpperCase();
        if (!"M".equals(value) && !"F".equals(value)) {
            throw new IllegalArgumentException("성별은 M 또는 F만 입력할 수 있습니다.");
        }
        return value;
    }

    private boolean hasAnyAddress(UsersDTO user) {
        return StringUtils.hasText(user.getUserZipCode())
                || StringUtils.hasText(user.getUserBaseAddress())
                || StringUtils.hasText(user.getUserDetailAddress());
    }
}
