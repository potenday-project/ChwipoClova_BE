package com.chwipoClova.qa.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class QaEditor {

    private String answer;

    @Builder
    public QaEditor(String answer) {
        this.answer = answer;
    }
}
