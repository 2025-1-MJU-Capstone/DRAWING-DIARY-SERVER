package com.example.capstone.diary;

import com.example.capstone.auth.CustomUserDetails;
import com.example.capstone.diary.dto.*;
import com.example.capstone.member.Member;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryResponse> createDiary(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @RequestBody @Valid DiaryRequest request) {
        DiaryResponse response = diaryService.createDiary(request, userDetails.getMember());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DiaryListResponse>> getDiaries(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Integer year,
            @RequestParam Integer month) {

        Member member = userDetails.getMember();
        List<DiaryListResponse> responses = diaryService.getDiaries(member, year, month);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryDetailResponse> getDiary(@PathVariable Long id,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        DiaryDetailResponse response = diaryService.getDiary(id, member);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        diaryService.deleteDiary(id, member);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/font")
    public ResponseEntity<Void> updateDiaryFont(
            @PathVariable("id") Long diaryId,
            @RequestBody UpdateDiaryFontRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        diaryService.updateDiaryFont(diaryId, request.getFontId(), member);

        return ResponseEntity.ok().build();
    }
}
