package com.btg.challenge.friend;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    FriendDto toDto(Friend friend);

    Friend toEntity(FriendDto friendDto);
}
