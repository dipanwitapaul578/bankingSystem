package com.bank.service;

import com.bank.dto.AccountRequest;
import com.bank.dto.AccountResponse;
import com.bank.model.Account;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponse createAccount(String email, AccountRequest request) {

        // Find the user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check user doesn't already have this account type
        boolean exists = accountRepository.findByUserId(user.getId())
                .stream()
                .anyMatch(acc -> acc.getAccountType() == request.getAccountType());

        if (exists) {
            throw new RuntimeException("You already have a " + request.getAccountType() + " account");
        }

        // Build account
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(Account.Status.ACTIVE);
        account.setUser(user);
        account.setIfscCode(request.getIfscCode());
        account.setBranchAddress(request.getBranchAddress());

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public AccountResponse getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = String.valueOf((long)(Math.random() * 9_000_000_000L) + 1_000_000_000L);
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType().name(),
                account.getBalance(),
                account.getStatus().name(),
                account.getCreatedAt()
        );
    }
}