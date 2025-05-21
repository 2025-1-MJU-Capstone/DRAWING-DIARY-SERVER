package com.example.capstone.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * ~/api/auth/oauth/kakao/login 입력 후 인가 코드 복사
 * /api/auth/oauth에 입력
 */
@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
public class KakaoController {

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    // 1. 인가 코드 요청
    @GetMapping("/kakao/login")
    public ResponseEntity<Void> redirectToKakaoLogin() {
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 redirect
    }

    // 2. callback (인가 코드 확인)
    @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        return ResponseEntity.ok("인가 코드: " + code);
    }
}