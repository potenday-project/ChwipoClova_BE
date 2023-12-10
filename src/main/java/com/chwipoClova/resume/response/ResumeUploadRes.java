package com.chwipoClova.resume.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumeUploadRes {

    @Schema(description = "아이디", example = "1", name = "userId")
    private Long userId;

    @Schema(description = "이력서 번호", example = "1", name = "resumeId")
    private Long resumeId;
}
