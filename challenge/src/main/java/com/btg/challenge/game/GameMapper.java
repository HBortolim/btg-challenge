package com.btg.challenge.game;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameDto toDto(Game game);

    @Mapping(target = "id", ignore = true)
    Game toEntity(GameDto gameDto);
}
