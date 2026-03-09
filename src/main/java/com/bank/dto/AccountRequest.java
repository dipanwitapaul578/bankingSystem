package com.bank.dto;

import com.bank.model.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountRequest {
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;

    @NotBlank(message = "IFSC code is required")
    private String ifscCode;

    @NotBlank(message = "Branch address is required")
    private String branchAddress;
}