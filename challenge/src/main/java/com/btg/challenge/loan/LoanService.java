package com.btg.challenge.loan;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.btg.challenge.friend.Friend;
import com.btg.challenge.friend.FriendRepository;
import com.btg.challenge.game.Game;
import com.btg.challenge.game.GameRepository;
import com.btg.challenge.shared.exception.ResourceNotFoundException;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final FriendRepository friendRepository;
    private final GameRepository gameRepository;
    private final LoanMapper loanMapper;

    public LoanService(LoanRepository loanRepository, FriendRepository friendRepository, GameRepository gameRepository, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.friendRepository = friendRepository;
        this.gameRepository = gameRepository;
        this.loanMapper = loanMapper;
    }

    public LoanDto createLoan(LoanRequestDto loanRequest) {
        Friend friend = friendRepository.findById(loanRequest.getFriendId())
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));
        Game game = gameRepository.findById(loanRequest.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        Loan loan = new Loan();
        loan.setFriend(friend);
        loan.setGame(game);
        loan.setLoanDate(LocalDate.now());

        return loanMapper.toDto(loanRepository.save(loan));
    }

    public Page<LoanDto> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable)
                .map(loanMapper::toDto);
    }

    public LoanDto returnLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        loan.setReturnDate(LocalDate.now());
        return loanMapper.toDto(loanRepository.save(loan));
    }
}
