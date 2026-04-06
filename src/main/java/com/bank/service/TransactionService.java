package com.bank.service;

import com.bank.dto.AmountRequest;
import com.bank.dto.TransactionResponse;
import com.bank.dto.TransferRequest;
import com.bank.dto.UpdateTransactionRequest;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse deposit(String accountNumber, AmountRequest request) {

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() == Account.Status.FROZEN) {
            throw new RuntimeException("Account is frozen");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setToAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.Type.DEPOSIT);
        transaction.setDescription("Deposit to " + accountNumber);
        transaction.setCategory(request.getCategory() != null ? request.getCategory() : "DEPOSIT");

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, AmountRequest request) {

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than zero");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() == Account.Status.FROZEN) {
            throw new RuntimeException("Account is frozen");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setToAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.Type.WITHDRAWAL);
        transaction.setDescription("Withdrawal from " + accountNumber);
        transaction.setCategory(request.getCategory() != null ? request.getCategory() : "WITHDRAWAL");

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
                Account.AccountType type = request.getAccountType() != null
                        ? request.getAccountType() : Account.AccountType.SAVINGS;
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
        transaction.setCategory(request.getCategory() != null ? request.getCategory() : "TRANSFER");

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    public List<TransactionResponse> getTransactionHistory(
            String accountNumber,
            String type,
            String category,
            String startDate,
            String endDate) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction.Type transactionType = null;
        if (type != null && !type.isEmpty()) {
            transactionType = Transaction.Type.valueOf(type.toUpperCase());
        }

        LocalDateTime start = startDate != null && !startDate.isEmpty()
                ? LocalDateTime.parse(startDate + "T00:00:00") : null;
        LocalDateTime end = endDate != null && !endDate.isEmpty()
                ? LocalDateTime.parse(endDate + "T23:59:59") : null;

        return transactionRepository
                .findFilteredTransactions(account.getId(), transactionType, category, start, end)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (request.getCategory() != null) {
            transaction.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transactionRepository.delete(transaction);
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
        return "******" + accountNumber.substring(accountNumber.length() - 4);
    }

    private String buildDescription(Transaction t) {
        String toName = t.getToAccount().getUser().getFullName();
        String fromName = t.getFromAccount() != null
                ? t.getFromAccount().getUser().getFullName() : "Cash";

        return switch (t.getType()) {
            case DEPOSIT -> "Deposit by " + fromName;
            case WITHDRAWAL -> "Withdrawal by " + toName;
            case TRANSFER -> "Transfer from " + fromName + " to " + toName;
        };
    }
}