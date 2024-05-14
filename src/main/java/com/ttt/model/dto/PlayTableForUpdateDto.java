package com.ttt.model.dto;

import com.ttt.model.SingleField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayTableForUpdateDto {
    private List<SingleField> singleFields;
}
