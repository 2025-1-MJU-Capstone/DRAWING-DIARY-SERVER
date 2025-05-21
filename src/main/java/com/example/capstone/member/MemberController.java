package com.example.capstone.member;

import com.example.capstone.auth.CustomUserDetails;
import com.example.capstone.member.dto.MemberInfoResponse;
import com.example.capstone.member.dto.UpdatePasswordRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(memberService.getMyInfo(member));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequest request,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();

        memberService.updatePassword(member, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();

        memberService.deleteMember(member);
        return ResponseEntity.ok().build();
    }
}
