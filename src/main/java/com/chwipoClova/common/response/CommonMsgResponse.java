package com.chwipoClova.common.response;

import lombok.Data;

@Data
public class CommonMsgResponse {
    private String message;

    public CommonMsgResponse(String message) {
        this.message = message;
    }
}
