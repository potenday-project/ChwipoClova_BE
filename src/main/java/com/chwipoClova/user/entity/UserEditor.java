package com.chwipoClova.user.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class UserEditor {

    private String name;
    private Date modifyDate;

    @Builder
    public UserEditor(String name, Date modifyDate) {
        this.name = name;
        this.modifyDate = modifyDate;
    }
}
