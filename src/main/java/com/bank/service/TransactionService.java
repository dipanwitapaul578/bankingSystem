package com.bank.service;

import com.bank.dto.TransactionResponse;
import com.bank.dto.TransferRequest;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse deposit(String accountNumber, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() == Account.Status.FROZEN) {
            throw new RuntimeException("Account is frozen");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setToAccount(account);
        transaction.setAmount(amount);
        transaction.setType(Transaction.Type.DEPOSIT);
        transaction.setDescription("Deposit to " + accountNumber);

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() == Account.Status.FROZEN) {
            throw new RuntimeException("Account is frozen");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setToAccount(account);
        transaction.setAmount(amount);
        transaction.setType(Transaction.Type.WITHDRAWAL);
        transaction.setDescription("Withdrawal from " + accountNumber);

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }

        Account fromAccount;
        Account toAccount;

        switch (request.getTransferMode()) {

            case ACCOUNT -> {
                if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
                    throw new RuntimeException("Cannot transfer to the same account");
                }
                fromAccount = accountRepository
                        .findByAccountNumberWithLock(request.getFromAccountNumber())
                        .orElseThrow(() -> new RuntimeException("Source account not found"));
                toAccount = accountRepository
                        .findByAccountNumberWithLock(request.getToAccountNumber())
                        .orElseThrow(() -> new RuntimeException("Destination account not found"));
            }

            case PHONE -> {
                Account.AccountType type = request.getAccountType() != null ? request.getAccountType() : Account.AccountType.SAVINGS;
                fromAccount = accountRepository
                        .findByUserPhoneNumberAndType(request.getFromPhoneNumber(), type)
                        .orElseThrow(() -> new RuntimeException("No active account found"));
                toAccount = accountRepository
                        .findByUserPhoneNumberAndType(request.getToPhoneNumber(), type)
                        .orElseThrow(() -> new RuntimeException("No active account found"));
            }

            case IFSC -> {
                fromAccount = accountRepository
                        .findByAccountNumberWithLock(request.getFromAccountNumber())
                        .orElseThrow(() -> new RuntimeException("Source account not found"));
                toAccount = accountRepository
                        .findByAccountNumberAndIfscCode(
                                request.getToAccountNumber(),
                                request.getToIfscCode())
                        .orElseThrow(() -> new RuntimeException("No account found with given IFSC and account number"));
            }

            default -> throw new RuntimeException("Invalid transfer mode");
        }

        if (fromAccount.getStatus() == Account.Status.FROZEN) {
            throw new RuntimeException("Source account is frozen");
        }

        if (toAccount.getStatus() == Account.Status.FROZEN) {
            throw new RuntimeException("Destination account is frozen");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.Type.TRANSFER);
        transaction.setDescription(request.getDescription() != null
                ? request.getDescription()
                : "Transfer from " + fromAccount.getUser().getFullName()
                + " to " + toAccount.getUser().getFullName()
                + " via " + request.getTransferMode().name());

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    public List<TransactionResponse> getTransactionHistory(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> sent = transactionRepository
                .findByFromAccountId(account.getId());
        List<Transaction> received = transactionRepository
                .findByToAccountId(account.getId());

        return Stream.concat(sent.stream(), received.stream())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getFromAccount() != null ? maskAccountNumber(t.getFromAccount().getAccountNumber()) : null,
                maskAccountNumber(t.getToAccount().getAccountNumber()),
                t.getAmount(),
                t.getType().name(),
                buildDescription(t),
                t.getCreatedAt()
        );
    }

    private String maskAccountNumber(String accountNumber) {
        // Shows only last 4 digits → ******6789
        return "******" + accountNumber.substring(accountNumber.length() - 4);
    }

    private String buildDescription(Transaction t) {
        String toName = t.getToAccount().getUser().getFullName();
        String fromName = t.getFromAccount() != null ? t.getFromAccount().getUser().getFullName() : "Cash";

        return switch (t.getType()) {
            case DEPOSIT -> "Deposit by " + fromName;
            case WITHDRAWAL -> "Withdrawal by " + toName;
            case TRANSFER -> "Transfer from " + fromName + " to " + toName;
        };
    }
}