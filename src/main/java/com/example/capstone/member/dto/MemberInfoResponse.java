package com.example.capstone.member.dto;

import com.example.capstone.auth.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoResponse {
    private String loginId;
    private String email;
    private SocialProvider socialProvider;
}
