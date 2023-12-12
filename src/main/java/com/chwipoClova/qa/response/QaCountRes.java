package com.chwipoClova.qa.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QaCountRes {

    @Schema(description = "사용개수", example = "1", name = "useCnt")
    private Integer useCnt;

    @Schema(description = "총개수", example = "10", name = "totalCnt")
    private Integer totalCnt;

    @Schema(description = "마지막 질문 번호", example = "1", name = "lastQaId")
    private Long lastQaId;
}
