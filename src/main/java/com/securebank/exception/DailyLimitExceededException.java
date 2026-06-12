package com.securebank.exception;

import java.math.BigDecimal;

public class DailyLimitExceededException extends RuntimeException {
    public DailyLimitExceededException(BigDecimal currentTotal, BigDecimal limit, BigDecimal attemptedAmount) {
        super(String.format("Daily limit exceeded. Current: ₹%.2f, Limit: ₹%.2f, Attempted: ₹%.2f", 
              currentTotal.doubleValue(), limit.doubleValue(), attemptedAmount.doubleValue()));
    }
}