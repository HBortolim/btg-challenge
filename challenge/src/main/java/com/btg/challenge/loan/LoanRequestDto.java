package com.btg.challenge.loan;

public class LoanRequestDto {
    private Long friendId;
    private Long gameId;

    public LoanRequestDto() {
    }

    public LoanRequestDto(Long friendId, Long gameId) {
        this.friendId = friendId;
        this.gameId = gameId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
