package com.ttt.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleMove {
    private int playTableId;
    private int singleFieldPosition;
    private SingleFieldStatus singleFieldStatus;
}