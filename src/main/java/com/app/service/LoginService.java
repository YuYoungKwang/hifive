package com.app.service;

import com.app.dto.UsersDTO;

public interface LoginService {
    UsersDTO register(String userId,
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
                     String userDetailAddress);

    String findUserIdByEmail(String email);

    String resetPassword(String userId, String email);

    UsersDTO findByAuthId(String userId);

    boolean isSocialUserMissingAdditionalInfo(String userId);

    void updateSocialAdditionalInfo(String userId, String userPhoneNumber, String userRegistrationNo);
}
