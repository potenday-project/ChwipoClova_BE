package com.chwipoClova.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserLoginType {
    KAKAO(1, "카카오")
    ;

    private final Integer code;
    private final String name;

    private static final UserLoginType[] VALUES;

    static {
        VALUES = values();
    }

    public static String getLoginTypeName(Integer code) {
        for (UserLoginType status : VALUES) {
            if (status.code == code) {
                return status.getName();
            }
        }
        return "";
    }
}
