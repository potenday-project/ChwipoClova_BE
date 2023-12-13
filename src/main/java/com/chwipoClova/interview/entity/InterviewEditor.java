package com.chwipoClova.interview.entity;

import lombok.Builder;
import lombok.Getter;


@Getter
public class InterviewEditor {

    private Integer status;

    @Builder
    public InterviewEditor(Integer status) {
        this.status = status;
    }
}
