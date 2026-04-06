package com.bank.repository;

import com.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByToAccountId(Long accountId);
    List<Transaction> findByFromAccountId(Long accountId);
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findFilteredTransactions(
            @Param("accountId") Long accountId,
            @Param("type") Transaction.Type type,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}