package com.bank.service;

import com.bank.dto.AccountResponse;
import com.bank.dto.TransactionResponse;
import com.bank.dto.UserResponse;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole().name(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getAccountType().name(),
                        account.getBalance(),
                        account.getStatus().name(),
                        account.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public AccountResponse freezeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus(Account.Status.FROZEN);
        Account saved = accountRepository.save(account);

        return new AccountResponse(
                saved.getId(),
                saved.getAccountNumber(),
                saved.getAccountType().name(),
                saved.getBalance(),
                saved.getStatus().name(),
                saved.getCreatedAt()
        );
    }

    public AccountResponse unfreezeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus(Account.Status.ACTIVE);
        Account saved = accountRepository.save(account);

        return new AccountResponse(
                saved.getId(),
                saved.getAccountNumber(),
                saved.getAccountType().name(),
                saved.getBalance(),
                saved.getStatus().name(),
                saved.getCreatedAt()
        );
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(t -> new TransactionResponse(
                        t.getId(),
                        t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null,
                        t.getToAccount().getAccountNumber(),
                        t.getAmount(),
                        t.getType().name(),
                        t.getDescription(),
                        t.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}