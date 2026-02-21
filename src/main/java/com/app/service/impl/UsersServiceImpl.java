package com.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.app.dao.UsersDAO;
import com.app.dto.TagMasterDTO;
import com.app.dto.UserTagMapDTO;
import com.app.dto.UsersDTO;
import com.app.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	UsersDAO usersDAO;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UsersDTO getUserByAuthId(String authId) {
		UsersDTO usersDTO = usersDAO.getUserByAuthId(authId);
		return usersDTO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public UsersDTO updateProfileByAuthId(String authId, UsersDTO users) {
		String safeAuthId = normalizeAuthId(authId);
		UsersDTO current = usersDAO.getUserByAuthId(safeAuthId);
		if (current == null) {
			throw new IllegalArgumentException("해당 회원을 찾을 수 없습니다.");
		}
		if (users == null) {
			throw new IllegalArgumentException("저장할 회원정보가 없습니다.");
		}

		UsersDTO normalized = sanitizeProfileInput(users);

		int updatedRows = usersDAO.updateUserProfileByAuthId(safeAuthId, normalized);
		if (updatedRows == 0) {
			throw new IllegalArgumentException("회원정보 저장에 실패했습니다.");
		}

		if (hasAnyAddressInput(normalized) || hasExistingAddress(current)) {
			usersDAO.upsertUserAddressByAuthId(safeAuthId, normalized);
		}

		return usersDAO.getUserByAuthId(safeAuthId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changePasswordByAuthId(String authId, String currentPassword, String newPassword) {
		String safeAuthId = normalizeAuthId(authId);
		if (!StringUtils.hasText(currentPassword) || !StringUtils.hasText(newPassword)) {
			throw new IllegalArgumentException("현재 비밀번호와 새 비밀번호를 모두 입력해 주세요.");
		}

		String currentPasswordTrimmed = currentPassword.trim();
		String newPasswordTrimmed = newPassword.trim();
		if (newPasswordTrimmed.length() < 8) {
			throw new IllegalArgumentException("새 비밀번호는 8자 이상이어야 합니다.");
		}
		if (currentPasswordTrimmed.equals(newPasswordTrimmed)) {
			throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
		}

		String storedPassword = usersDAO.findAuthPasswordByAuthId(safeAuthId);
		if (!StringUtils.hasText(storedPassword)) {
			throw new IllegalArgumentException("비밀번호 정보를 확인할 수 없습니다.");
		}
		if (!passwordEncoder.matches(currentPasswordTrimmed, storedPassword)) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}

		String encodedPassword = passwordEncoder.encode(newPasswordTrimmed);
		int updatedRows = usersDAO.updateAuthPasswordByAuthId(safeAuthId, encodedPassword);
		if (updatedRows != 1) {
			throw new IllegalArgumentException("비밀번호 변경에 실패했습니다.");
		}
	}

	@Override
	public String findUserName(int userNo) {
		return usersDAO.getUserNickname(userNo);
	}

	@Override
	public List<UserTagMapDTO> getUserTagsByAuthId(String authId) {
		return usersDAO.getUserTagsByAuthId(authId);
	}

	@Override
	public List<TagMasterDTO> getTagMasterList() {
		return usersDAO.getTagMasterList();
	}

	@Override
	public boolean addUserTagByAuthId(String authId, String tagCode) {
		String safeAuthId = normalizeAuthId(authId);
		String safeTagCode = normalizeTagCode(tagCode);
		if (usersDAO.countTagMasterByTagCode(safeTagCode) <= 0) {
			throw new IllegalArgumentException("존재하지 않는 태그 코드는 사용할 수 없습니다.");
		}
		return usersDAO.insertUserTagByAuthId(safeAuthId, safeTagCode, "MANUAL") > 0;
	}

	@Override
	public boolean removeUserTagByAuthId(String authId, String tagCode) {
		String safeAuthId = normalizeAuthId(authId);
		String safeTagCode = normalizeTagCode(tagCode);
		return usersDAO.deleteUserTagByAuthId(safeAuthId, safeTagCode) > 0;
	}

	private String normalizeAuthId(String authId) {
		if (authId == null || authId.trim().isEmpty()) {
			throw new IllegalArgumentException("로그인 상태가 유효하지 않습니다.");
		}
		String trimmed = authId.trim();
		return trimmed.length() <= 100 ? trimmed : trimmed.substring(0, 100);
	}

	private String normalizeTagCode(String tagCode) {
		if (tagCode == null || tagCode.trim().isEmpty()) {
			throw new IllegalArgumentException("태그 코드가 비어 있습니다.");
		}
		String trimmed = tagCode.trim();
		if (trimmed.length() > 50) {
			trimmed = trimmed.substring(0, 50);
		}
		return trimmed;
	}

	private UsersDTO sanitizeProfileInput(UsersDTO users) {
		UsersDTO normalized = new UsersDTO();

		String userName = safeText(users.getUserName(), 100);
		String userNickName = safeText(users.getUserNickName(), 50);
		String userPhone = safeText(users.getUserPhoneNumber(), 50);
		String userRegNo = safeText(users.getUserRegistrationNo(), 100);
		String zipCode = safeText(users.getUserZipCode(), 6);
		String baseAddress = safeText(users.getUserBaseAddress(), 255);
		String detailAddress = safeText(users.getUserDetailAddress(), 255);
		String gender = normalizeGender(users.getUserGender());

		if (!StringUtils.hasText(userName)) {
			throw new IllegalArgumentException("이름은 필수 입력값입니다.");
		}
		if (!StringUtils.hasText(userNickName)) {
			throw new IllegalArgumentException("닉네임은 필수 입력값입니다.");
		}

		normalized.setUserName(userName);
		normalized.setUserNickName(userNickName);
		normalized.setUserGender(gender);
		normalized.setUserPhoneNumber(userPhone);
		normalized.setUserRegistrationNo(userRegNo);
		normalized.setUserZipCode(zipCode);
		normalized.setUserBaseAddress(baseAddress);
		normalized.setUserDetailAddress(detailAddress);
		return normalized;
	}

	private String safeText(String value, int maxLength) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
	}

	private String normalizeGender(String userGender) {
		if (!StringUtils.hasText(userGender)) {
			return null;
		}
		String value = userGender.trim().toUpperCase();
		return ("M".equals(value) || "F".equals(value)) ? value : null;
	}

	private boolean hasAnyAddressInput(UsersDTO users) {
		return StringUtils.hasText(users.getUserZipCode())
				|| StringUtils.hasText(users.getUserBaseAddress())
				|| StringUtils.hasText(users.getUserDetailAddress());
	}

	private boolean hasExistingAddress(UsersDTO users) {
		return users != null && users.getUserAddressNo() != null;
	}

}
