package com.chwipoClova.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserLoginRes {

    @Schema(description = "아이디", example = "1", name = "userId")
    private Long userId;

    @Schema(description = "이름", example = "홍길동", name = "name")
    private String name;

    @Schema(description = "이메일", example = "test@naver.com", name = "email")
    private String email;

    @Schema(description = "가입일", example = "2023-12-09T10:13:17.838+00:00", name = "regDate")
    private Date regDate;

    @Schema(description = "수정일", example = "2023-12-09T10:13:17.838+00:00", name = "modifyDate")
    private Date modifyDate;

    @Schema(description = "소셜 로그인 플랫폼 (1 : 카카오)", example = "1", name = "snsType")
    private Integer snsType;

    @Schema(description = "소셜회원 ID", example = "11314", name = "snsId")
    private Long snsId;
}
