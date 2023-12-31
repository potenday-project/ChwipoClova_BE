package com.chwipoClova.resume.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResumeUploadReq {

    @Schema(description = "유저 Id", example = "1", name = "userId")
    private Long userId;

}
