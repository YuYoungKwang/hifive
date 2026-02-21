package com.app.dao.auth;

import com.app.dto.UserDTO;

public interface AuthDAO {
    boolean existsAuthId(String userId);

    boolean existsEmail(String email);

    Long nextUserNo();

    Long nextUserAuthNo();

    Long nextUserAddressNo();

    int insertUser(UserDTO user);

    int insertUserAuthentication(UserDTO user, Long userAuthNo, String encodedPassword);

    int insertUserAddress(UserDTO user);

    UserDTO findByAuthId(String userId);

    String findAuthIdByEmail(String email);

    int updatePasswordByAuthIdAndEmail(String userId, String email, String encodedPassword);
}
