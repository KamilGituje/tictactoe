package com.ttt.model.dto;

import com.ttt.model.PlaySide;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayTableForCreationDto {
    private int edgeSize;
    private PlaySide cpuSide;
    private int winningRowSize;
}
