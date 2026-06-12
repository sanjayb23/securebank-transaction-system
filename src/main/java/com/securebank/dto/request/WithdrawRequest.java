package com.securebank.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private Long accountId;
    private BigDecimal amount;
    private String description;

    // Explicit getters to fix compilation issues
    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}