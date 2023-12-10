package com.chwipoClova.resume.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResumeDeleteReq {

    @Schema(description = "이력서 ID", example = "1", name = "resumeId")
    private Long resumeId;

    @Schema(description = "유저 ID", example = "1", name = "userId")
    private Long userId;

}
