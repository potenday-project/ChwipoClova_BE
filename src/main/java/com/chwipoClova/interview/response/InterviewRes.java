package com.chwipoClova.interview.response;

import com.chwipoClova.qa.response.QaListForFeedbackRes;
import com.chwipoClova.qa.response.QaListRes;
import com.chwipoClova.qa.response.QaQuestionInsertRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class InterviewRes {

    @Schema(description = "면접 ID", example = "1", name = "interviewId")
    private Long interviewId;
    @Schema(description = "유저 ID", example = "1", name = "userId")
    private Long userId;
    @Schema(description = "면접 제목", example = "삼성채용", name = "title")
    private String title;
    @Schema(description = "완료여부(0 미완료, 1 완료)", example = "1", name = "status")
    private Integer status;
    @Schema(description = "등록일", example = "2023-12-09T10:13:17.838+00:00", name = "regDate")
    private Date regDate;
    @Schema(description = "면접관의속마음", example = "속마음1", name = "feedback")
    private String feedback;
    @Schema(description = "질문데이터", name = "qaData")
    private List<QaListForFeedbackRes> qaData;

}
