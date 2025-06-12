package com.example.capstone.diary;

import com.example.capstone.diary.dto.DiaryRequest;
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

    public String generateImage(DiaryRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("diaryDate", request.getDiaryDate().toString());
        body.put("title", request.getTitle());
        body.put("content", request.getContent());
        body.put("feeling", request.getFeeling());
        body.put("color", request.getColor());
        body.put("customStyle", request.getCustomStyle());

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
