package com.example.capstone.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * ~/api/auth/oauth/naver/login 입력 후 인가 코드 복사
 * /api/auth/oauth에 입력
 */
@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
public class NaverController {

    @Value("${oauth.naver.client-id}")
    private String clientId;

    @Value("${oauth.naver.redirect-uri}")
    private String redirectUri;

    // 1. 인가 코드 요청
    @GetMapping("/naver/login")
    public ResponseEntity<Void> redirectToNaverLogin() {
        String state = UUID.randomUUID().toString(); // CSRF 방지용

        String url = "https://nid.naver.com/oauth2.0/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&state=" + state;

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // 2. callback (인가 코드 확인)
    @GetMapping("/naver/callback")
    public ResponseEntity<String> naverCallback(@RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok("인가 코드: " + code + ", state: " + state);
    }
}