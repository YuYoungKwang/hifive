package com.app.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

@Configuration
@PropertySource(value = "classpath:oauth2.properties", ignoreResourceNotFound = true)
public class OAuth2Config {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Config.class);

    @Value("${oauth2.google.client-id:GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret:GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Value("${oauth2.kakao.client-id:KAKAO_REST_API_KEY}")
    private String kakaoClientId;

    @Value("${oauth2.kakao.client-secret:KAKAO_CLIENT_SECRET}")
    private String kakaoClientSecret;

    @Value("${oauth2.naver.client-id:NAVER_CLIENT_ID}")
    private String naverClientId;

    @Value("${oauth2.naver.client-secret:NAVER_CLIENT_SECRET}")
    private String naverClientSecret;

    @Value("${oauth2.base-url:}")
    private String oauth2BaseUrl;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();
        registrations.add(googleRegistration());
        registrations.add(kakaoRegistration());
        registrations.add(naverRegistration());
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    private ClientRegistration googleRegistration() {
        warnIfPlaceholder("google", googleClientId, googleClientSecret);

        return ClientRegistration.withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(resolveRedirectUri())
                .scope("openid", "profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .build();
    }

    private ClientRegistration kakaoRegistration() {
        warnIfPlaceholder("kakao", kakaoClientId, kakaoClientSecret);

        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId("kakao")
                .clientId(kakaoClientId)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(resolveRedirectUri())
                .scope("profile_nickname", "account_email")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientName("Kakao");

        applyClientSecret(builder, kakaoClientSecret);
        return builder.build();
    }

    private ClientRegistration naverRegistration() {
        warnIfPlaceholder("naver", naverClientId, naverClientSecret);

        return ClientRegistration.withRegistrationId("naver")
                .clientId(naverClientId)
                .clientSecret(naverClientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(resolveRedirectUri())
                .scope("name", "email", "profile_image")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .clientName("Naver")
                .build();
    }

    private String resolveRedirectUri() {
        if (!StringUtils.hasText(oauth2BaseUrl)) {
            return "{baseUrl}/login/oauth2/code/{registrationId}";
        }
        return oauth2BaseUrl.trim() + "/login/oauth2/code/{registrationId}";
    }

    private void applyClientSecret(ClientRegistration.Builder builder, String secret) {
        if (StringUtils.hasText(secret) && !isPlaceholder(secret)) {
            builder.clientSecret(secret);
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
            return;
        }
        builder.clientAuthenticationMethod(ClientAuthenticationMethod.NONE);
    }

    private void warnIfPlaceholder(String provider, String clientId, String clientSecret) {
        if (isPlaceholder(clientId) || isPlaceholder(clientSecret)) {
            log.warn("OAuth2 {} credentials are placeholders. Update src/main/resources/oauth2.properties first.", provider);
        }
    }

    private boolean isPlaceholder(String value) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        String normalized = value.trim().toUpperCase();
        return normalized.contains("CLIENT_ID")
                || normalized.contains("CLIENT_SECRET")
                || normalized.contains("REST_API_KEY");
    }
}
