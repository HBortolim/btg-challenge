package com.btg.challenge.loan;

import com.btg.challenge.friend.Friend;
import com.btg.challenge.friend.FriendRepository;
import com.btg.challenge.game.Game;
import com.btg.challenge.game.GameRepository;
import com.btg.challenge.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LoanServiceTest {
    @Mock
    private LoanRepository loanRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private GameRepository gameRepository;

    @Spy
    private LoanMapper loanMapper = Mappers.getMapper(LoanMapper.class);

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenCreateLoanWithValidDataShouldReturnLoanDto() {
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(1L);
        loanRequestDto.setGameId(1L);

        Friend friend = new Friend();
        friend.setId(1L);

        Game game = new Game();
        game.setId(1L);

        Loan loan = new Loan();
        loan.setFriend(friend);
        loan.setGame(game);

        LoanDto loanDto = new LoanDto();
        loanDto.setFriendId(1L);
        loanDto.setGameId(1L);

        when(friendRepository.findById(1L)).thenReturn(Optional.of(friend));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanMapper.toDto(any(Loan.class))).thenReturn(loanDto);

        loanService.createLoan(loanRequestDto);

        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void whenCreateLoanWithNonExistentFriendShouldThrowResourceNotFoundException() {
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(1L);

        when(friendRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.createLoan(loanRequestDto));
    }

    @Test
    public void whenCreateLoanWithNonExistentGameShouldThrowResourceNotFoundException() {
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(1L);
        loanRequestDto.setGameId(1L);

        Friend friend = new Friend();
        friend.setId(1L);

        when(friendRepository.findById(1L)).thenReturn(Optional.of(friend));
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.createLoan(loanRequestDto));
    }

    @Test
    public void whenReturnLoanWithValidIdShouldMarkAsReturned() {
        Loan loan = new Loan();
        loan.setId(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        loanService.returnLoan(1L);

        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void whenReturnLoanWithNonExistentIdShouldThrowResourceNotFoundException() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.returnLoan(1L));
    }
}
