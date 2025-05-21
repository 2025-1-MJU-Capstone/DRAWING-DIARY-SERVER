package com.example.capstone.diary;

import com.example.capstone.diary.dto.DiaryDetailResponse;
import com.example.capstone.diary.dto.DiaryListResponse;
import com.example.capstone.diary.dto.DiaryRequest;
import com.example.capstone.diary.dto.DiaryResponse;
import com.example.capstone.font.Font;
import com.example.capstone.font.FontRepository;
import com.example.capstone.global.exception.ForbiddenException;
import com.example.capstone.global.exception.NotFoundException;
import com.example.capstone.member.Member;
import com.example.capstone.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final FontRepository fontRepository;
    private final DiaryAiClient diaryAiClient;
    private final S3Uploader s3Uploader;

    @Transactional
    public DiaryResponse createDiary(DiaryRequest request, Member member) {

        Font font = fontRepository.findById(request.getFontId())
                .orElseThrow(() -> new NotFoundException("폰트를 찾을 수 없습니다."));

        String base64Image = diaryAiClient.generateImage(request.getFeeling(), request.getColor(), request.getCustomStyle());
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        String imageFileName = member.getId() + "/diary/" + UUID.randomUUID() + ".png";

        String s3Url = s3Uploader.upload(imageBytes, imageFileName);

        Diary diary = Diary.builder()
                .member(member)
                .font(font)
                .diaryDate(request.getDiaryDate())
                .title(request.getTitle())
                .content(request.getContent())
                .imageFileName(imageFileName)
                .imageUrl(s3Url)
                .build();

        diaryRepository.save(diary);

        return new DiaryResponse(
                diary.getId(),
                diary.getImageUrl(),
                diary.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<DiaryListResponse> getDiaries(Member member, Integer year, Integer month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Diary> diaries = diaryRepository.findByMemberAndDiaryDateBetween(member, start, end);

        return diaries.stream()
                .map(diary -> new DiaryListResponse(
                        diary.getId(),
                        diary.getDiaryDate(),
                        diary.getTitle()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiaryDetailResponse getDiary(Long id, Member member) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("일기를 찾을 수 없습니다."));

        if (!diary.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("본인의 일기만 조회할 수 있습니다.");
        }

        return new DiaryDetailResponse(
                diary.getDiaryDate(),
                diary.getTitle(),
                diary.getContent(),
                diary.getImageUrl(),
                diary.getFont() != null ? diary.getFont().getId() : null,
                diary.getFont() != null ? diary.getFont().getFontName() : null,
                diary.getCreatedAt()
        );
    }

    @Transactional
    public void deleteDiary(Long id, Member member) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("일기를 찾을 수 없습니다."));

        if (!diary.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("해당 일기를 삭제할 권한이 없습니다.");
        }

        s3Uploader.delete(diary.getImageFileName());

        diaryRepository.delete(diary);
    }

    @Transactional
    public void updateDiaryFont(Long diaryId, Long fontId, Member member) {
        Diary diary = diaryRepository.findByIdAndMember(diaryId, member)
                .orElseThrow(() -> new NotFoundException("일기를 찾을 수 없습니다."));

        Font font = fontRepository.findByIdAndMember(fontId, member)
                .orElseThrow(() -> new NotFoundException("폰트를 찾을 수 없습니다."));

        diary.changeFont(font);
    }
}
