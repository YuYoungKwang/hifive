package com.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.dao.AuthDAO;
import com.app.dto.UsersDTO;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private static final String ACTIVE_STATUS = "A";

    private final AuthDAO authDAO;

    @Autowired
    public CustomUserDetailsService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersDTO user = authDAO.findByAuthId(username);
        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
        }
        if (!ACTIVE_STATUS.equalsIgnoreCase(user.getUserStatus())) {
            throw new DisabledException("활성 상태가 아닌 계정입니다.");
        }
        if (user.getAuthPassword() == null) {
            throw new UsernameNotFoundException("비밀번호가 등록되지 않은 계정입니다.");
        }

        return User.withUsername(user.getAuthId())
                .password(user.getAuthPassword())
                .roles("USER")
                .build();
    }
}
