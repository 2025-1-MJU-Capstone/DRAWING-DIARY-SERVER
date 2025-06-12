package com.example.capstone.auth.dto;

import com.example.capstone.auth.SocialProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OauthLoginRequest {

    private String provider;

    @NotBlank
    private String code;

    private String state;
}
