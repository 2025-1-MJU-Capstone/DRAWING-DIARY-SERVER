package com.example.capstone.diary;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DiaryAiClient {

    @Value("${diary.url}")
    private String url;

    private final RestTemplate restTemplate;

    public String generateImage(String feeling, String color, String customStyle) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("feeling", feeling);
        body.put("color", color);
        body.put("customStyle", customStyle);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return (String) response.getBody().get("base64_image");
        } else {
            throw new IllegalStateException("이미지 생성 실패");
        }
    }
}
