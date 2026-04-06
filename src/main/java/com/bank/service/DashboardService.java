package com.bank.service;

import com.bank.dto.DashboardResponse;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DashboardResponse getDashboardSummary(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> received = transactionRepository.findByToAccountId(account.getId());
        List<Transaction> sent = transactionRepository.findByFromAccountId(account.getId());

        // Total income — all deposits and incoming transfers
        BigDecimal totalIncome = received.stream()
                .filter(t -> t.getType() == Transaction.Type.DEPOSIT
                        || t.getType() == Transaction.Type.TRANSFER)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total expenses — all withdrawals and outgoing transfers
        BigDecimal totalExpenses = Stream.concat(
                sent.stream().filter(t -> t.getType() == Transaction.Type.TRANSFER),
                received.stream().filter(t -> t.getType() == Transaction.Type.WITHDRAWAL)
        ).map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Net balance
        BigDecimal netBalance = account.getBalance();

        // Category wise totals
        Map<String, BigDecimal> categoryTotals = Stream.concat(sent.stream(), received.stream())
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount, BigDecimal::add)
                ));

        // Recent activity — last 5 transactions
        List<String> recentActivity = Stream.concat(sent.stream(), received.stream())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(t -> t.getType().name() + " of ₹" + t.getAmount()
                        + " on " + t.getCreatedAt().toLocalDate())
                .collect(Collectors.toList());

        // Monthly trends — group by month
        Map<String, BigDecimal> monthlyTrends = Stream.concat(sent.stream(), received.stream())
                .collect(Collectors.groupingBy(
                        t -> t.getCreatedAt().getYear() + "-"
                                + String.format("%02d", t.getCreatedAt().getMonthValue()),
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount, BigDecimal::add)
                ));

        return new DashboardResponse(
                totalIncome,
                totalExpenses,
                netBalance,
                categoryTotals,
                recentActivity,
                monthlyTrends
        );
    }
}