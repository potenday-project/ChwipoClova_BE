package com.chwipoClova.resume.response;

import lombok.Data;

import java.util.List;

@Data
public class ApiResult {
    private ApiResultMessage message;

    private String stopReason;
    private Integer inputLength;
    private Integer outputLength;

    private List<ApiAiFilter> aiFilter;
}
