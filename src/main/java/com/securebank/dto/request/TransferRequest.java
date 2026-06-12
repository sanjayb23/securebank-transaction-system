package com.securebank.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Long fromAccountId;
    private String toAccountNumber;
    private BigDecimal amount;
    private String description;

    // Explicit getters to fix compilation issues
    public Long getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}