package com.example.capstone.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DiaryListResponse {
    private Long id;
    private LocalDate diaryDate;
    private String title;
}
