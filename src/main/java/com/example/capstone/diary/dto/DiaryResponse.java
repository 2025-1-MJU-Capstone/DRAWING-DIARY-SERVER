package com.example.capstone.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DiaryResponse {
    private Long id;
    private String imageUrl;
    private LocalDateTime createdAt;
}
