package com.bank.dto;

import lombok.Data;

@Data
public class UpdateTransactionRequest {

    private String category;
    private String description;
}
