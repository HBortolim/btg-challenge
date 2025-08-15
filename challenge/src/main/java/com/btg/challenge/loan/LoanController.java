package com.btg.challenge.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanDto> createLoan(@RequestBody LoanRequestDto loanRequest) {
        return ResponseEntity.status(201).body(loanService.createLoan(loanRequest));
    }

    @GetMapping
    public ResponseEntity<Page<LoanDto>> getAllLoans(Pageable pageable) {
        return ResponseEntity.ok(loanService.getAllLoans(pageable));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDto> returnLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnLoan(id));
    }
}
