package com.chwipoClova.qa.request;


import com.chwipoClova.interview.entity.Interview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QaQuestionInsertReq {

    @Schema(description = "면접객체", name = "interview")
    private Interview interview;

    @Schema(description = "질문", example = "질문1", name = "question")
    private String question;

    @Schema(description = "AI 답변", example = "AI답변1", name = "aiAnswer")
    private String aiAnswer;

}
