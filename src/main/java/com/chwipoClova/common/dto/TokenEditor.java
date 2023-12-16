package com.chwipoClova.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class TokenEditor {
    private String refreshToken;

    @Builder
    public TokenEditor(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
