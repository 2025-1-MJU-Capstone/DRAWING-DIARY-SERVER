package com.example.capstone.auth;

import com.example.capstone.member.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 없으면 빈 리스트
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return member.getPassword(); // 로컬 로그인용
    }

    @Override
    public String getUsername() {
        return member.getLoginId() != null ? member.getLoginId() : member.getEmail();
    }
}
