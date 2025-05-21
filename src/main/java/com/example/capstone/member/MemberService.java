package com.example.capstone.member;

import com.example.capstone.auth.RefreshTokenRepository;
import com.example.capstone.global.exception.BadRequestException;
import com.example.capstone.global.exception.ForbiddenException;
import com.example.capstone.member.dto.MemberInfoResponse;
import com.example.capstone.member.dto.UpdatePasswordRequest;
import com.example.capstone.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Uploader s3Uploader;

    public MemberInfoResponse getMyInfo(Member member) {
        return new MemberInfoResponse(
                member.getLoginId(),
                member.getEmail(),
                member.getSocialProvider()
        );
    }

    @Transactional
    public void updatePassword(Member member, UpdatePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new ForbiddenException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new BadRequestException("새 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
            throw new BadRequestException("새 비밀번호가 현재 비밀번호와 같습니다.");
        }

        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void deleteMember(Member member) {
        s3Uploader.deleteAllByMemberId(member.getId());
        refreshTokenRepository.deleteByMember(member);
        memberRepository.delete(member);
    }


}
