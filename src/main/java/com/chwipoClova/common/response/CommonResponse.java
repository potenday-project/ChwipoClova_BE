package com.chwipoClova.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommonResponse<T> {

    private final String code;

    private final T data;

    private final String message;

}
