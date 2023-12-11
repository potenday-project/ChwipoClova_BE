package com.chwipoClova.feedback.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FeedbackListReq {

    @Schema(description = "질문답변 ID", example = "1", name = "qaId")
    private Long qaId;

}
