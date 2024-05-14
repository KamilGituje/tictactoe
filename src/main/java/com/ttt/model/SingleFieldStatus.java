package com.ttt.model;

import lombok.Getter;

@Getter
public enum SingleFieldStatus {
    blank(0),
    circle(1),
    cross(2);
    final int id;
    private SingleFieldStatus(int id)
    {
        this.id = id;
    }

}
