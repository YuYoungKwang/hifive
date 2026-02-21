package com.app.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component("oauth2LoginFailureHandler")
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2LoginFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String reasonCode = resolveReasonCode(exception);
        String encodedReason = URLEncoder.encode(reasonCode, StandardCharsets.UTF_8.name());
        String targetUrl = request.getContextPath() + "/auth/login?oauth2Error=" + encodedReason;

        log.warn("OAuth2 login failed. reasonCode={}", reasonCode, exception);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String resolveReasonCode(AuthenticationException exception) {
        String message = exception.getMessage() == null ? "" : exception.getMessage().toLowerCase();
        if (message.contains("invalid_client")) {
            return "invalid_client";
        }
        if (message.contains("redirect_uri_mismatch")) {
            return "redirect_uri_mismatch";
        }
        if (message.contains("access_denied")) {
            return "access_denied";
        }
        if (message.contains("invalid_scope")) {
            return "invalid_scope";
        }
        return "oauth2_login_failed";
    }
}
