package com.example.capstone.diary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DiaryRequest {

    @NotNull
    private LocalDate diaryDate;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Long fontId;

    @NotBlank
    private String feeling;

    @NotBlank
    private String color;

    private String customStyle;

}