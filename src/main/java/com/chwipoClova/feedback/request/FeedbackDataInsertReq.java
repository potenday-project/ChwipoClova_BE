package com.chwipoClova.feedback.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FeedbackDataInsertReq {

    @Schema(description = "피드백타입", example = "1", name = "type")
    private Integer type;

    @Schema(description = "피드백내용", example = "피드백1입니다.", name = "content")
    private String content;
}
