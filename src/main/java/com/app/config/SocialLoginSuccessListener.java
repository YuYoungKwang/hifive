package com.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.app.service.impl.SocialUserProvisionService;

@Component
public class SocialLoginSuccessListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private static final Logger log = LoggerFactory.getLogger(SocialLoginSuccessListener.class);

    private final SocialUserProvisionService socialUserProvisionService;

    @Autowired
    public SocialLoginSuccessListener(SocialUserProvisionService socialUserProvisionService) {
        this.socialUserProvisionService = socialUserProvisionService;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            return;
        }

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        if (!(token.getPrincipal() instanceof OAuth2User)) {
            return;
        }

        OAuth2User oAuth2User = (OAuth2User) token.getPrincipal();

        try {
            socialUserProvisionService.provisionIfMissing(
                    token.getAuthorizedClientRegistrationId(),
                    oAuth2User.getAttributes());
        } catch (Exception e) {
            // Do not block login on provisioning failure; keep app accessible.
            log.error("Failed to provision social user for provider={}",
                    token.getAuthorizedClientRegistrationId(), e);
        }
    }
}
