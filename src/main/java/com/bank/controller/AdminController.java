package com.bank.controller;

import com.bank.dto.AccountResponse;
import com.bank.dto.TransactionResponse;
import com.bank.dto.UserResponse;
import com.bank.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @PutMapping("/accounts/{accountNumber}/freeze")
    public ResponseEntity<AccountResponse> freezeAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(adminService.freezeAccount(accountNumber));
    }

    @PutMapping("/accounts/{accountNumber}/unfreeze")
    public ResponseEntity<AccountResponse> unfreezeAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(adminService.unfreezeAccount(accountNumber));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }
}