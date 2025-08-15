package com.btg.challenge.loan;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "friend.id", target = "friendId")
    @Mapping(source = "game.id", target = "gameId")
    LoanDto toDto(Loan loan);

    @Mapping(source = "friendId", target = "friend.id")
    @Mapping(source = "gameId", target = "game.id")
    Loan toEntity(LoanDto loanDto);
}
