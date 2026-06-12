package com.securebank.dto.response;

import com.securebank.enums.TransactionStatus;
import com.securebank.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private String transactionId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal fee;
    private String description;
    private String fromAccountNumber;
    private String toAccountNumber;
    private TransactionStatus status;
    private LocalDateTime createdAt;

    // Explicit setters to fix compilation issues
    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }
}