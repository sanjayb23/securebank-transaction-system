package com.securebank.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    private final BigDecimal available;
    private final BigDecimal required;

    public InsufficientBalanceException(BigDecimal available, BigDecimal required) {
        super(String.format("Insufficient balance. Available: ₹%.2f, Required: ₹%.2f", 
              available.doubleValue(), required.doubleValue()));
        this.available = available;
        this.required = required;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public BigDecimal getRequired() {
        return required;
    }
}