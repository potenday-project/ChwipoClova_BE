package com.chwipoClova.feedback.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FeedbackListRes {
    @Schema(description = "질문답변 ID", example = "1", name = "qaId")
    private Long qaId;

    @Schema(description = "피드백 ID", example = "1", name = "feedbackId")
    private Long feedbackId;

    @Schema(description = "피드백 타입", example = "1", name = "type")
    private Integer type;

    @Schema(description = "피드백 내용", example = "피드백입니다.", name = "content")
    private String content;

    @Schema(description = "등록일", example = "2023-12-09T10:13:17.838+00:00", name = "regDate")
    private Date regDate;

    @Schema(description = "수정일", example = "2023-12-09T10:13:17.838+00:00", name = "modifyDate")
    private Date modifyDate;

}
