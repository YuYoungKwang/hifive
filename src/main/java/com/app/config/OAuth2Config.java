/*
 * package com.app.config;
 * 
 * import java.util.Arrays;
 * 
 * import org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.security.oauth2.client.registration.*; import
 * org.springframework.security.oauth2.client.*;
 * 
 * @Configuration public class OAuth2Config {
 * 
 * @Bean public ClientRegistrationRepository clientRegistrationRepository() {
 * 
 * ClientRegistration kakao = ClientRegistration .withRegistrationId("kakao")
 * .clientId("여기에_카카오_REST_API_KEY") .clientSecret("여기에_CLIENT_SECRET")
 * .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
 * .redirectUri("{baseUrl}/login/oauth2/code/kakao")
 * .authorizationUri("https://kauth.kakao.com/oauth/authorize")
 * .tokenUri("https://kauth.kakao.com/oauth/token")
 * .userInfoUri("https://kapi.kakao.com/v2/user/me")
 * .userNameAttributeName("id") .clientName("Kakao") .scope("profile_nickname",
 * "account_email") .build();
 * 
 * return new InMemoryClientRegistrationRepository(Arrays.asList(kakao)); } }
 */