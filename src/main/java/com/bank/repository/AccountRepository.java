package com.bank.repository;

import com.bank.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUserId(Long userId);
    boolean existsByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.user.phoneNumber = :phoneNumber AND a.status = 'ACTIVE' AND a.accountType = :accountType")
    Optional<Account> findByUserPhoneNumberAndType(
            @Param("phoneNumber") String phoneNumber,
            @Param("accountType") Account.AccountType accountType
    );

    // Find account by IFSC + account number
    Optional<Account> findByAccountNumberAndIfscCode(String accountNumber, String ifscCode);
}
