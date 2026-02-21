package com.app.dao;

import com.app.dto.UsersDTO;

public interface AuthDAO {
    boolean existsAuthId(String userId);

    boolean existsEmail(String email);

    Long nextUserNo();

    Long nextUserAuthNo();

    Long nextUserAddressNo();

    int insertUser(UsersDTO user);

    int insertUserAuthentication(UsersDTO user, Long userAuthNo, String encodedPassword);

    int insertUserAddress(UsersDTO user);

    UsersDTO findByAuthId(String userId);

    String findAuthIdByEmail(String email);

    int updatePasswordByAuthIdAndEmail(String userId, String email, String encodedPassword);

    int updateSocialAdditionalInfoByAuthId(String userId, String userPhoneNumber, String userRegistrationNo);
}
