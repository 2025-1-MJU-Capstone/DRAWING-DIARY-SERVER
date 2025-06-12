package com.example.capstone.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String email;
}
