package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.app.dto.UsersDTO;
import com.app.service.UsersService;

@ControllerAdvice
public class HeaderModelAdvice {

    @Autowired
    private UsersService usersService;

    @ModelAttribute("headerDisplayName")
    public String headerDisplayName(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String authId = authentication.getName();
        if (authId == null || authId.trim().isEmpty()) {
            return null;
        }

        UsersDTO usersDTO = usersService.getUserByAuthId(authId.trim());
        if (usersDTO == null) {
            return authId.trim();
        }
        if (usersDTO.getUserNickName() != null && !usersDTO.getUserNickName().trim().isEmpty()) {
            return usersDTO.getUserNickName().trim();
        }
        if (usersDTO.getUserName() != null && !usersDTO.getUserName().trim().isEmpty()) {
            return usersDTO.getUserName().trim();
        }
        return authId.trim();
    }
}
