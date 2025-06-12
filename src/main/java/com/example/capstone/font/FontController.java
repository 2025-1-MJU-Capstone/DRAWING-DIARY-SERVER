package com.example.capstone.font;

import com.example.capstone.auth.CustomUserDetails;
import com.example.capstone.font.dto.FontResponse;
import com.example.capstone.member.Member;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/fonts")
@RequiredArgsConstructor
public class FontController {

    private final FontService fontService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateFont(
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam("fontName") String fontName,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        Member member = userDetails.getMember();

        Font font = fontService.generateFont(images, fontName, member);

        FontResponse response = new FontResponse(font.getId(), font.getFontName(), font.getTtfUrl(), font.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FontResponse>> getMyFonts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();

        List<FontResponse> fonts = fontService.getMyFonts(member);
        return ResponseEntity.ok(fonts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFont(@PathVariable("id") Long fontId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();

        fontService.deleteFont(fontId, member);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/image")
//    public ResponseEntity<Resource> downloadInputImage(@AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
//        Resource inputImage = fontService.getInputImage();
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inputImage.png")
//                .contentType(MediaType.IMAGE_PNG)
//                .contentLength(inputImage.contentLength())
//                .body(inputImage);
//        }

    @GetMapping("/image")
    public ResponseEntity<Map<String, String>> downloadInputImage() {
        Map<String, String> response = new HashMap<>();
        response.put("url", fontService.getInputImageUrl());
        return ResponseEntity.ok(response);
    }
}