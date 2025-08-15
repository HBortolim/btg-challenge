package com.btg.challenge.loan;

import com.btg.challenge.shared.config.JwtTokenProvider;
import com.btg.challenge.shared.exception.GlobalExceptionHandler;
import com.btg.challenge.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(LoanController.class)
@Import({LoanController.class, GlobalExceptionHandler.class})
public class LoanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanService loanService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "user")
    public void whenCreateLoanWithValidDataShouldReturnCreatedResponse() throws Exception {
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(1L);
        loanRequestDto.setGameId(1L);

        LoanDto loanDto = new LoanDto();
        loanDto.setFriendId(1L);
        loanDto.setGameId(1L);

        when(loanService.createLoan(any(LoanRequestDto.class))).thenReturn(loanDto);

        mockMvc.perform(post("/loans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetAllLoansShouldReturnOkResponse() throws Exception {
        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenReturnLoanWithValidIdShouldReturnOkResponse() throws Exception {
        mockMvc.perform(put("/loans/1/return")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenReturnLoanWithNonExistentIdShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Loan not found")).when(loanService).returnLoan(1L);

        mockMvc.perform(put("/loans/1/return")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenCreateLoanWithNonExistentFriendShouldReturnNotFound() throws Exception {
        // Given
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(999L);
        loanRequestDto.setGameId(1L);

        when(loanService.createLoan(any(LoanRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Friend not found"));

        // When & Then
        mockMvc.perform(post("/loans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Friend not found"));
    }

    @Test
    @WithMockUser(username = "user")
    public void whenCreateLoanWithNonExistentGameShouldReturnNotFound() throws Exception {
        // Given
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(1L);
        loanRequestDto.setGameId(999L);

        when(loanService.createLoan(any(LoanRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Game not found"));

        // When & Then
        mockMvc.perform(post("/loans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Game not found"));
    }

    @Test
    @WithMockUser(username = "user")
    public void whenCreateLoanWithValidDataShouldReturnCreatedLoanDto() throws Exception {
        // Given
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(1L);
        loanRequestDto.setGameId(2L);

        LoanDto loanDto = new LoanDto();
        loanDto.setId(1L);
        loanDto.setFriendId(1L);
        loanDto.setGameId(2L);

        when(loanService.createLoan(any(LoanRequestDto.class))).thenReturn(loanDto);

        // When & Then
        mockMvc.perform(post("/loans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.friendId").value(1))
                .andExpect(jsonPath("$.gameId").value(2));

        verify(loanService, times(1)).createLoan(any(LoanRequestDto.class));
    }

    @Test
    @WithMockUser(username = "user")
    public void whenCreateLoanWithInvalidJsonShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/loans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(loanService);
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetAllLoansWithPaginationShouldReturnPagedResponse() throws Exception {
        // Given
        LoanDto loan1 = new LoanDto();
        loan1.setId(1L);
        loan1.setFriendId(1L);
        loan1.setGameId(1L);

        LoanDto loan2 = new LoanDto();
        loan2.setId(2L);
        loan2.setFriendId(2L);
        loan2.setGameId(2L);

        Page<LoanDto> loanPage = new PageImpl<>(Arrays.asList(loan1, loan2));
        when(loanService.getAllLoans(any())).thenReturn(loanPage);

        // When & Then
        mockMvc.perform(get("/loans")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));

        verify(loanService, times(1)).getAllLoans(any());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenGetAllLoansWithEmptyResultShouldReturnEmptyPage() throws Exception {
        // Given
        Page<LoanDto> emptyPage = new PageImpl<>(Collections.emptyList());
        when(loanService.getAllLoans(any())).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.empty").value(true));

        verify(loanService, times(1)).getAllLoans(any());
    }

    @Test
    @WithMockUser(username = "user")
    public void whenReturnLoanSuccessfullyShouldReturnUpdatedLoan() throws Exception {
        // Given
        LoanDto returnedLoan = new LoanDto();
        returnedLoan.setId(1L);
        returnedLoan.setFriendId(1L);
        returnedLoan.setGameId(1L);

        when(loanService.returnLoan(1L)).thenReturn(returnedLoan);

        // When & Then
        mockMvc.perform(put("/loans/1/return")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.friendId").value(1))
                .andExpect(jsonPath("$.gameId").value(1));

        verify(loanService, times(1)).returnLoan(1L);
    }

    @Test
    @WithMockUser(username = "user")
    public void whenReturnLoanWithInvalidIdShouldReturnNotFound() throws Exception {
        // Given
        when(loanService.returnLoan(999L))
                .thenThrow(new ResourceNotFoundException("Loan not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/loans/999/return")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Loan not found with id: 999"));

        verify(loanService, times(1)).returnLoan(999L);
    }

    @Test
    public void whenCreateLoanWithoutAuthenticationShouldReturnUnauthorized() throws Exception {
        // Given
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(1L);
        loanRequestDto.setGameId(1L);

        // When & Then
        mockMvc.perform(post("/loans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(loanService);
    }

    @Test
    public void whenGetAllLoansWithoutAuthenticationShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/loans"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(loanService);
    }

    @Test
    public void whenReturnLoanWithoutAuthenticationShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(put("/loans/1/return")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(loanService);
    }

    @Test
    @WithMockUser(username = "user")
    public void whenCreateLoanWithoutCsrfShouldReturnForbidden() throws Exception {
        // Given
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setFriendId(1L);
        loanRequestDto.setGameId(1L);

        // When & Then
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(loanService);
    }
}
