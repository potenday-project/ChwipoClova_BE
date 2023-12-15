package com.chwipoClova.qa.response;

import com.chwipoClova.feedback.response.FeedbackListRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class QaListForFeedbackRes {

    @Schema(description = "면접 ID", example = "1", name = "interviewId")
    private Long interviewId;

    @Schema(description = "질문 ID", example = "1", name = "qaId")
    private Long qaId;

    @Schema(description = "질문", example = "질문1", name = "question")
    private String question;

    @Schema(description = "답변", example = "답변1", name = "answer")
    private String answer;

    @Schema(description = "등록일", example = "2023-12-09T10:13:17.838+00:00", name = "regDate")
    private Date regDate;

    @Schema(description = "수정일", example = "2023-12-09T10:13:17.838+00:00", name = "modifyDate")
    private Date modifyDate;

    @Schema(description = "피드백1 키워드", example = "키워드", name = "keyword")
    private String keyword;

    @Schema(description = "피드백2 모법답안", example = "AI 답변", name = "bestAnswer")
    private String bestAnswer;
}
