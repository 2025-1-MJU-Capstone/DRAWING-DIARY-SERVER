package com.example.capstone.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NaverOauthClient {

    private final RestTemplate restTemplate;

    @Value("${oauth.naver.client-id}")
    private String clientId;

    @Value("${oauth.naver.client-secret}")
    private String clientSecret;

    @Value("${oauth.naver.redirect-uri}")
    private String redirectUri;

    public String getAccessTokenFromCode(String code, String state) {
        String url = "https://nid.naver.com/oauth2.0/token"
                + "?grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&code=" + code
                + "&state=" + state;

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("네이버 access token 요청 실패");
        }

        Map<String, Object> body = response.getBody();
        return (String) body.get("access_token");
    }

    public NaverUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("네이버 인증 실패");
        }

        Map<String, Object> body = response.getBody();
        Map<String, Object> data = (Map<String, Object>) body.get("response");

        String id = (String) data.get("id");
        String email = (String) data.get("email");

        return new NaverUserInfo(id, email);
    }

    public record NaverUserInfo(String id, String email) {}
}
