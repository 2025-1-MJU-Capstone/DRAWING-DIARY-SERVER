package com.example.capstone.font.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FontResponse {
    private Long id;
    private String fontName;
    private String ttfUrl;
    private LocalDateTime createdAt;
}

