package com.chwipoClova.interview.entity;

import lombok.Builder;
import lombok.Getter;


@Getter
public class InterviewEditor {

    private Integer status;

    private String feedback;

    @Builder
    public InterviewEditor(Integer status, String feedback) {
        this.status = status;
        this.feedback = feedback;
    }
}
