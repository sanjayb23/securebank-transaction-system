package com.securebank.dto.request;

import com.securebank.enums.AccountType;
import lombok.Data;

@Data
public class CreateAccountRequest {
    private AccountType accountType;
    private String currency;

    // Explicit getters to fix compilation issues
    public AccountType getAccountType() {
        return accountType;
    }

    public String getCurrency() {
        return currency;
    }
}