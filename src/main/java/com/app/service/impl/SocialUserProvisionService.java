package com.app.service.impl;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.app.dao.AuthDAO;
import com.app.dto.UsersDTO;

@Service
public class SocialUserProvisionService {

    private static final String ACTIVE_STATUS = "A";
    private static final String SOCIAL_USER_TYPE = "SOCIAL";
    private static final int AUTH_ID_MAX_LENGTH = 100;
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int USER_NAME_MAX_LENGTH = 100;
    private static final int NICKNAME_MAX_LENGTH = 20;
    private static final int PROFILE_IMG_MAX_LENGTH = 255;

    private final AuthDAO authDAO;

    @Autowired
    public SocialUserProvisionService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void provisionIfMissing(String provider, Map<String, Object> attributes) {
        String normalizedProvider = normalizeProvider(provider);
        Map<String, Object> safeAttributes = attributes == null ? Collections.emptyMap() : attributes;

        String authId = resolveSocialAuthId(normalizedProvider, safeAttributes);
        if (!StringUtils.hasText(authId)) {
            throw new IllegalArgumentException("Missing provider user id: " + normalizedProvider);
        }

        if (authDAO.findByAuthId(authId) != null) {
            return;
        }

        String email = normalizeEmail(extractEmail(normalizedProvider, safeAttributes));
        if (!StringUtils.hasText(email) || email.length() > EMAIL_MAX_LENGTH || authDAO.existsEmail(email)) {
            email = buildSyntheticEmail(authId);
        }

        String userName = truncate(firstNonBlank(
                extractName(normalizedProvider, safeAttributes),
                normalizedProvider + "_user"), USER_NAME_MAX_LENGTH);
        String nickName = truncate(firstNonBlank(
                extractNickname(normalizedProvider, safeAttributes),
                userName), NICKNAME_MAX_LENGTH);

        UsersDTO user = new UsersDTO();
        user.setUserNo(authDAO.nextUserNo());
        user.setUserType(SOCIAL_USER_TYPE);
        user.setUserStatus(ACTIVE_STATUS);
        user.setUserName(userName);
        user.setUserNickName(nickName);
        user.setUserProfileImg(truncate(
                normalizeBlank(extractProfileImage(normalizedProvider, safeAttributes)),
                PROFILE_IMG_MAX_LENGTH));
        user.setAuthId(authId);
        user.setAuthEmail(email);
        user.setAuthSnsType(normalizedProvider.toUpperCase(Locale.ROOT));

        Long userAuthNo = authDAO.nextUserAuthNo();

        try {
            int insertedUsers = authDAO.insertUser(user);
            int insertedAuth = authDAO.insertUserAuthentication(user, userAuthNo, null);
            if (insertedUsers != 1 || insertedAuth != 1) {
                throw new IllegalStateException("Failed to provision social user: " + authId);
            }
        } catch (DataIntegrityViolationException e) {
            if (authDAO.findByAuthId(authId) != null) {
                return;
            }
            throw e;
        }
    }

    public String resolveSocialAuthId(String provider, Map<String, Object> attributes) {
        String normalizedProvider = normalizeProvider(provider);
        Map<String, Object> safeAttributes = attributes == null ? Collections.emptyMap() : attributes;

        String providerUserId = extractProviderUserId(normalizedProvider, safeAttributes);
        if (!StringUtils.hasText(providerUserId)) {
            return null;
        }
        return buildAuthId(normalizedProvider, providerUserId);
    }

    private String normalizeProvider(String provider) {
        if (!StringUtils.hasText(provider)) {
            return "social";
        }
        return provider.trim().toLowerCase(Locale.ROOT);
    }

    private String extractProviderUserId(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return firstNonBlank(asString(attributes.get("sub")), asString(attributes.get("id")));
        }
        if ("kakao".equals(provider)) {
            return asString(attributes.get("id"));
        }
        if ("naver".equals(provider)) {
            Map<String, Object> response = asMap(attributes.get("response"));
            return firstNonBlank(asString(response.get("id")), asString(attributes.get("id")));
        }

        return firstNonBlank(
                asString(attributes.get("id")),
                asString(attributes.get("sub")),
                asString(attributes.get("email")));
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = asMap(attributes.get("kakao_account"));
            return asString(kakaoAccount.get("email"));
        }
        if ("naver".equals(provider)) {
            Map<String, Object> response = asMap(attributes.get("response"));
            return asString(response.get("email"));
        }
        return asString(attributes.get("email"));
    }

    private String extractName(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = asMap(attributes.get("kakao_account"));
            return firstNonBlank(
                    asString(kakaoAccount.get("name")),
                    extractNickname(provider, attributes));
        }
        if ("naver".equals(provider)) {
            Map<String, Object> response = asMap(attributes.get("response"));
            return firstNonBlank(
                    asString(response.get("name")),
                    asString(response.get("nickname")));
        }
        return firstNonBlank(asString(attributes.get("name")), asString(attributes.get("login")));
    }

    private String extractNickname(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = asMap(attributes.get("kakao_account"));
            Map<String, Object> profile = asMap(kakaoAccount.get("profile"));
            return firstNonBlank(
                    asString(profile.get("nickname")),
                    asString(kakaoAccount.get("profile_nickname")));
        }
        if ("naver".equals(provider)) {
            Map<String, Object> response = asMap(attributes.get("response"));
            return firstNonBlank(asString(response.get("nickname")), asString(response.get("name")));
        }
        return firstNonBlank(asString(attributes.get("given_name")), asString(attributes.get("name")));
    }

    private String extractProfileImage(String provider, Map<String, Object> attributes) {
        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = asMap(attributes.get("kakao_account"));
            Map<String, Object> profile = asMap(kakaoAccount.get("profile"));
            return asString(profile.get("profile_image_url"));
        }
        if ("naver".equals(provider)) {
            Map<String, Object> response = asMap(attributes.get("response"));
            return asString(response.get("profile_image"));
        }
        return asString(attributes.get("picture"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeBlank(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String truncate(String value, int maxLength) {
        if (!StringUtils.hasText(value) || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String buildAuthId(String provider, String providerUserId) {
        String seed = provider + "_" + providerUserId.trim().replaceAll("\\s+", "");
        if (seed.length() <= AUTH_ID_MAX_LENGTH) {
            return seed;
        }

        String hash = Integer.toHexString(seed.hashCode());
        int prefixLength = AUTH_ID_MAX_LENGTH - hash.length() - 1;
        if (prefixLength < 1) {
            return hash;
        }
        return seed.substring(0, prefixLength) + "_" + hash;
    }

    private String buildSyntheticEmail(String authId) {
        String domain = "@social.local";
        int maxLocalLength = EMAIL_MAX_LENGTH - domain.length();
        String local = authId;
        if (local.length() > maxLocalLength) {
            local = local.substring(0, maxLocalLength);
        }
        return local + domain;
    }
}
