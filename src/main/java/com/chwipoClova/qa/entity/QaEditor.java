package com.chwipoClova.qa.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class QaEditor {

    private String answer;

    @Builder
    public QaEditor(String answer) {
        this.answer = answer;
    }
}
