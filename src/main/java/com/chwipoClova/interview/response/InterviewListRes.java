package com.chwipoClova.interview.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class InterviewListRes {

    @Schema(description = "면접 ID", example = "1", name = "interviewId")
    private Long interviewId;

    @Schema(description = "유저 ID", example = "1", name = "userId")
    private Long userId;

    @Schema(description = "면접 제목", example = "삼성채용", name = "title")
    private String title;

    @Schema(description = "총개수", example = "10", name = "totalCnt")
    private Integer totalCnt;

    @Schema(description = "답변개수", example = "1", name = "useCnt")
    private Integer useCnt;

    @Schema(description = "완료여부(0: 미완성, 1: 완성)", example = "1", name = "status")
    private Integer status;

    @Schema(description = "등록일", example = "2023-12-09T10:13:17.838+00:00", name = "regDate")
    private Date regDate;
}
