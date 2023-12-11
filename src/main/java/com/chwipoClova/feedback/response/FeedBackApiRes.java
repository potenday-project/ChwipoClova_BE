package com.chwipoClova.feedback.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FeedBackApiRes {

    @Schema(description = "질문 ID", example = "1", name = "qaId")
    private Long qaId;

    @Schema(description = "피드백 타입", example = "1", name = "type")
    private Integer type;

    @Schema(description = "피드백 내용", example = "피드백1입니다.", name = "content")
    private String content;

}
