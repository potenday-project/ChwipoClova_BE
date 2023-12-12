package com.chwipoClova.feedback.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class FeedbackEditor {

    private String content;

    @Builder
    public FeedbackEditor(String content) {
        this.content = content;
    }
}
