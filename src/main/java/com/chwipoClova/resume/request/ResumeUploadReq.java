package com.chwipoClova.resume.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ResumeUploadReq {

    @Schema(description = "이력서파일", example = "이력서.pdf", name = "file")
    private MultipartFile file;

    @Schema(description = "아이디", example = "1", name = "userId")
    private Long userId;

}
