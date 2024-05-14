package com.ttt.mapper;

import com.ttt.model.PlayTable;
import com.ttt.model.dto.PlayTableForUpdateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayTableMapper {
    PlayTable playTableForUpdateDtoToPlayTable(PlayTableForUpdateDto playTableForUpdateDto);
}
