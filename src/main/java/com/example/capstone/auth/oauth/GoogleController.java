package com.example.capstone.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * ~/api/auth/oauth/google/login 입력 후 인가 코드 복사
 * /api/auth/oauth에 입력
 */
@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
public class GoogleController {

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    // 1. 인가 코드 요청
    @GetMapping("/google/login")
    public ResponseEntity<Void> redirectToGoogleLogin() {
        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=openid%20email%20profile";

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Redirect
    }

    // 2. callback (인가 코드 확인)
    @GetMapping("/google/callback")
    public ResponseEntity<String> googleCallback(@RequestParam String code) {
        return ResponseEntity.ok("인가 코드: " + code);
    }
}
