package com.chwipoClova.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginReq {

    @Schema(description = "로그인코드", example = "1", name = "code")
    private String code;
}
