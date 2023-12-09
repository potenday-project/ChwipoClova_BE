package com.chwipoClova.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSnsUrlRes {

    @Schema(description = "SNS 로그인 URL", example = "https://sns.com", name = "url")
    private String url;
}
