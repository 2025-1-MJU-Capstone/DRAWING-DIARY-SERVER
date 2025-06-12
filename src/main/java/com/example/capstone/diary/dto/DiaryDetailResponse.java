package com.example.capstone.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DiaryDetailResponse {
    private LocalDate diaryDate;
    private String title;
    private String content;
    private String imageUrl;
    private Long fontId;
    private String fontName;
    private LocalDateTime createdAt;
}
