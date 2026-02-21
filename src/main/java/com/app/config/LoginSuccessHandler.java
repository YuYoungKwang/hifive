package com.app.config;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.app.service.LoginService;
import com.app.service.impl.SocialUserProvisionService;

@Component("loginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String DEFAULT_TARGET_URL = "/main";
    private static final String SOCIAL_ADDITIONAL_INFO_URL = "/auth/social/additional-info";

    private final LoginService loginService;
    private final SocialUserProvisionService socialUserProvisionService;

    @Autowired
    public LoginSuccessHandler(LoginService loginService,
                               SocialUserProvisionService socialUserProvisionService) {
        this.loginService = loginService;
        this.socialUserProvisionService = socialUserProvisionService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = DEFAULT_TARGET_URL;

        try {
            String authId = resolveAuthId(authentication);
            if (StringUtils.hasText(authId) && loginService.isSocialUserMissingAdditionalInfo(authId)) {
                targetUrl = SOCIAL_ADDITIONAL_INFO_URL;
            }
        } catch (Exception ignored) {
            targetUrl = DEFAULT_TARGET_URL;
        }

        response.sendRedirect(request.getContextPath() + targetUrl);
    }

    private String resolveAuthId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            Object principal = token.getPrincipal();
            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                Map<String, Object> attributes = oAuth2User.getAttributes();
                socialUserProvisionService.provisionIfMissing(token.getAuthorizedClientRegistrationId(), attributes);
                return socialUserProvisionService.resolveSocialAuthId(
                        token.getAuthorizedClientRegistrationId(),
                        attributes);
            }
        }
        return authentication.getName();
    }
}
