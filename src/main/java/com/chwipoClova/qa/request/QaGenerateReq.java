package com.chwipoClova.qa.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QaGenerateReq {
    @Schema(description = "유저 ID", example = "1", name = "userId")
    private Long userId;

    @Schema(description = "면접 ID", example = "1", name = "interviewId")
    private Long interviewId;
}
