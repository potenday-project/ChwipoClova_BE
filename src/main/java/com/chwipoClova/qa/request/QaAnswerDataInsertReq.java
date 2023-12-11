package com.chwipoClova.qa.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QaAnswerDataInsertReq {

    @Schema(description = "질문 ID", example = "1", name = "qaId")
    private Long qaId;

    @Schema(description = "답변", example = "답변1", name = "answer")
    private String answer;
}
