package com.chwipoClova.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@AllArgsConstructor
public enum ExceptionCode {

    BAD_REQUEST(String.valueOf(HttpStatus.BAD_REQUEST.value()), "잘못된 요청입니다."),

    NOT_FOUND(String.valueOf(HttpStatus.NOT_FOUND.value()), "요청한 페이지를 찾을 수 없습니다."),

    SERVER_ERROR(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "내부 서버 오류입니다."),



    // Custom Exception
    SECURITY("800", "로그인이 필요합니다"),

    USER_NULL("801", "유저 정보가 올바르지 않습니다."),

    FILE_EXT("850", "PDF 파일 형식이 아닙니다."),

    FILE_SIZE("851", "파일 업로드 최대 크기는 50M 입니다."),

    RESUME_NULL("860", "이력서 정보가 올바르지 않습니다.")

    ;

    private static final ExceptionCode[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String message;

    public static ExceptionCode resolve(String statusCode) {
        for (ExceptionCode status : VALUES) {
            if (status.code.equals(statusCode)) {
                return status;
            }
        }
        return null;
    }
}
