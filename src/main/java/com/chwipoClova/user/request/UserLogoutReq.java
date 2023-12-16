package com.chwipoClova.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserLogoutReq {
    @Schema(description = "유저 ID", example = "1", name = "userId")
    private Long userId;
}
