package com.chwipoClova.user.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class UsersEditor {

    private String name;
    private Date modifyDate;

    @Builder
    public UsersEditor(String name, Date modifyDate) {
        this.name = name;
        this.modifyDate = modifyDate;
    }
}
