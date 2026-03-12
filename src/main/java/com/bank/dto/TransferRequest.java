package com.bank.dto;

import com.bank.model.Account;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {

    // Transfer mode - ACCOUNT, PHONE, IFSC
    @NotNull(message = "Transfer mode is required")
    private TransferMode transferMode;

    // Used for ACCOUNT and IFSC mode
    private String fromAccountNumber;

    // Used for ACCOUNT mode
    private String toAccountNumber;

    // Used for PHONE mode
    private String fromPhoneNumber;
    private String toPhoneNumber;

    // Used for IFSC mode
    private String toIfscCode;

    // Used for PHONE mode
    private Account.AccountType accountType;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;

    public enum TransferMode {
        ACCOUNT, PHONE, IFSC
    }
}