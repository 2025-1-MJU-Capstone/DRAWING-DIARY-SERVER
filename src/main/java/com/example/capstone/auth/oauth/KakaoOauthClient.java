package com.example.capstone.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoOauthClient {

    private final RestTemplate restTemplate;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    public String getAccessTokenFromCode(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("카카오 access token 요청 실패");
        }

        Map<String, Object> responseBody = response.getBody();
        return (String) responseBody.get("access_token");
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("카카오 인증 실패");
        }

        Map<String, Object> body = response.getBody();

        Long id = ((Number) body.get("id")).longValue();
        Map<String, Object> account = (Map<String, Object>) body.get("kakao_account");
        String email = (String) account.get("email");

        return new KakaoUserInfo(id.toString(), email);
    }

    public record KakaoUserInfo(String id, String email) {}
}
