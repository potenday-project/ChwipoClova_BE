package com.chwipoClova.feedback.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FeedbackInsertReq {

    @Schema(description = "질문답변 ID", example = "1", name = "qaId")
    private Long qaId;

    @Schema(description = "질문", example = "질문1", name = "question")
    private String question;

    @Schema(description = "답변", example = "답변1", name = "answer")
    private String answer;

}
