package com.example.capstone.font;

import com.example.capstone.font.dto.FontResponse;
import com.example.capstone.global.exception.BadRequestException;
import com.example.capstone.global.exception.ForbiddenException;
import com.example.capstone.global.exception.NotFoundException;
import com.example.capstone.member.Member;
import com.example.capstone.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FontService {

    private final FontRepository fontRepository;
    private final FontAiClient fontAiClient;
    private final S3Uploader s3Uploader;
    private static final String IMAGE_PATH = "static/images/inputImage.png";

    @Transactional
    public Font generateFont(List<MultipartFile> images, String fontName, Member member) throws IOException {
        if (fontRepository.findByMember(member).size() >= 3) {
            throw new BadRequestException("최대 3개의 폰트만 생성할 수 있습니다.");
        }

        if (fontRepository.existsByMemberAndFontName(member, fontName)) {
            throw new BadRequestException("이미 존재하는 폰트 이름입니다.");
        }

        String base64Ttf = fontAiClient.generateFont(images);
        byte[] ttfBytes = Base64.getDecoder().decode(base64Ttf);

        String fontFileName = member.getId() + "/font/" + UUID.randomUUID() + ".ttf";

        String s3Url = s3Uploader.upload(ttfBytes, fontFileName);

        Font font = Font.builder()
                .fontName(fontName)
                .fontFileName(fontFileName)
                .ttfUrl(s3Url)
                .member(member)
                .build();

        return fontRepository.save(font);
    }

    public List<FontResponse> getMyFonts(Member member) {
        return fontRepository.findByMember(member).stream()
                .map(font -> new FontResponse(
                        font.getId(),
                        font.getFontName(),
                        font.getTtfUrl(),
                        font.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void deleteFont(Long fontId, Member member) {
        Font font = fontRepository.findById(fontId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 폰트입니다."));

        if (!font.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("해당 폰트를 삭제할 권한이 없습니다.");
        }

        s3Uploader.delete(font.getFontFileName());

        fontRepository.delete(font);
    }

    public Resource getInputImage() throws IOException {
        return new ClassPathResource(IMAGE_PATH);
    }
}
