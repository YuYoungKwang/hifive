package com.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.UserDAO;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
    private UserDAO userDAO;

    @Override
    public String findUserName(int userNo) {
        // DAO에게 닉네임을 가져오라고 시킵니다.
        return userDAO.getUserNickname(userNo);
    }
}
