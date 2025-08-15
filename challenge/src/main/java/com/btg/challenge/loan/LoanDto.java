package com.btg.challenge.loan;

import java.time.LocalDate;

public class LoanDto {
    private Long id;
    private Long friendId;
    private Long gameId;
    private LocalDate loanDate;
    private LocalDate returnDate;

    public LoanDto() {
    }

    public LoanDto(Long id, Long friendId, Long gameId, LocalDate loanDate, LocalDate returnDate) {
        this.id = id;
        this.friendId = friendId;
        this.gameId = gameId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
