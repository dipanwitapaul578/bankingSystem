package com.bank.controller;

import com.bank.dto.AmountRequest;
import com.bank.dto.TransactionResponse;
import com.bank.dto.TransferRequest;
import com.bank.dto.UpdateTransactionRequest;
import com.bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit/{accountNumber}")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(
                transactionService.deposit(accountNumber, request));
    }

    @PostMapping("/withdraw/{accountNumber}")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(
                transactionService.withdraw(accountNumber, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(
                transactionService.getTransactionHistory(
                        accountNumber, type, category, startDate, endDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @RequestBody UpdateTransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}