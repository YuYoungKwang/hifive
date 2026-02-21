package com.app.service;

import com.app.dto.UserDTO;

public interface LoginService {
    UserDTO register(String userId,
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

    UserDTO findByAuthId(String userId);
}
