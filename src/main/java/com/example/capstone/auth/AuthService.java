package com.example.capstone.auth;

import com.example.capstone.auth.dto.*;
import com.example.capstone.global.exception.BadRequestException;
import com.example.capstone.global.exception.UnauthorizedException;
import com.example.capstone.member.Member;
import com.example.capstone.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void signup(SignupRequest request) {
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new BadRequestException("이미 사용 중인 아이디입니다.");
        }

        if (memberRepository.existsByEmailAndSocialProvider(request.getEmail(), SocialProvider.LOCAL)) {
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        }

        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .socialProvider(SocialProvider.LOCAL)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new UnauthorizedException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenProvider.createToken(member.getLoginId());
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.deleteByMember(member);
        refreshTokenRepository.flush();

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .member(member)
                .build());

        return new LoginResponse(accessToken, refreshToken);
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 리프레시 토큰입니다."));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("만료된 리프레시 토큰입니다.");
        }

        Member member = refreshToken.getMember();
        String accessToken = tokenProvider.createToken(member.getLoginId());

        refreshTokenRepository.delete(refreshToken);

        String newRefreshToken = UUID.randomUUID().toString();
        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefreshToken)
                .member(member)
                .build());

        return new RefreshTokenResponse(accessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Member member) {
        refreshTokenRepository.deleteByMember(member);
    }
}
