package com.example.capstone.auth;

import com.example.capstone.auth.dto.LoginResponse;
import com.example.capstone.auth.dto.OauthLoginRequest;
import com.example.capstone.auth.oauth.GoogleOauthClient;
import com.example.capstone.auth.oauth.KakaoOauthClient;
import com.example.capstone.auth.oauth.NaverOauthClient;
import com.example.capstone.global.exception.BadRequestException;
import com.example.capstone.member.Member;
import com.example.capstone.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    private final GoogleOauthClient googleOauthClient;
    private final KakaoOauthClient kakaoOauthClient;
    private final NaverOauthClient naverOauthClient;

    @Transactional
    public LoginResponse login(OauthLoginRequest request) {
        SocialProvider provider = SocialProvider.valueOf(request.getProvider().toUpperCase());
        String code = request.getCode();

        String email;
        String socialId;
        String accessToken;

        if (provider == SocialProvider.GOOGLE) {
            accessToken = googleOauthClient.getAccessTokenFromCode(code);
            var userInfo = googleOauthClient.getUserInfo(accessToken);
            email = userInfo.email();
            socialId = userInfo.sub();
        } else if (provider == SocialProvider.KAKAO) {
            accessToken = kakaoOauthClient.getAccessTokenFromCode(code);
            var userInfo = kakaoOauthClient.getUserInfo(accessToken);
            email = userInfo.email();
            socialId = userInfo.id();
        } else if (provider == SocialProvider.NAVER) {
            String state = request.getState();
            accessToken = naverOauthClient.getAccessTokenFromCode(code, state);
            var userInfo = naverOauthClient.getUserInfo(accessToken);
            email = userInfo.email();
            socialId = userInfo.id();
        } else {
            throw new BadRequestException("지원하지 않는 소셜로그인입니다.");
        }

        Optional<Member> optionalMember = memberRepository.findBySocialProviderAndSocialId(provider, socialId);

        Member member = optionalMember.orElseGet(() -> {
            String loginId = provider.name().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 8);
            return memberRepository.save(Member.builder()
                    .loginId(loginId)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .email(email)
                    .socialId(socialId)
                    .socialProvider(provider)
                    .build());
        });

        String jwtAccessToken = tokenProvider.createToken(member.getLoginId());
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.deleteByMember(member);
        refreshTokenRepository.flush();

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .member(member)
                .build());

        return new LoginResponse(jwtAccessToken, refreshToken);
    }
}