package com.securebank.service;

import com.securebank.entity.Transaction;
import com.securebank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;

    private static final BigDecimal LARGE_AMOUNT_THRESHOLD = new BigDecimal("5000");
    private static final BigDecimal SPIKE_MULTIPLIER = new BigDecimal("10");
    private static final int MAX_TRANSACTIONS_PER_HOUR = 5;
    private static final int UNUSUAL_HOUR_START = 23; // 11 PM
    private static final int UNUSUAL_HOUR_END = 6;    // 6 AM

    public boolean isSuspicious(Transaction transaction) {
        // Get recent transactions for pattern analysis
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<Transaction> recentTransactions = transactionRepository
                .findRecentTransactionsByAccount(transaction.getFromAccount().getId(), oneDayAgo);

        // Check for multiple large transactions in short time
        if (hasMultipleLargeTransactions(recentTransactions, transaction)) {
            return true;
        }

        // Check for transactions at unusual hours
        if (isUnusualHour(transaction) && isLargeAmount(transaction.getAmount())) {
            return true;
        }

        // Check for sudden spike in transaction amounts
        if (hasSuddenAmountSpike(recentTransactions, transaction)) {
            return true;
        }

        // Check for rapid successive transactions
        if (hasRapidSuccessiveTransactions(recentTransactions)) {
            return true;
        }

        return false;
    }

    private boolean hasMultipleLargeTransactions(List<Transaction> recentTransactions, Transaction currentTransaction) {
        long largeTransactionCount = recentTransactions.stream()
                .filter(t -> isLargeAmount(t.getAmount()))
                .count();

        if (isLargeAmount(currentTransaction.getAmount())) {
            largeTransactionCount++;
        }

        return largeTransactionCount >= 3; // 3 or more large transactions in 24 hours
    }

    private boolean isUnusualHour(Transaction transaction) {
        int hour = transaction.getCreatedAt().getHour();
        return hour >= UNUSUAL_HOUR_START || hour <= UNUSUAL_HOUR_END;
    }

    private boolean isLargeAmount(BigDecimal amount) {
        return amount.compareTo(LARGE_AMOUNT_THRESHOLD) >= 0;
    }

    private boolean hasSuddenAmountSpike(List<Transaction> recentTransactions, Transaction currentTransaction) {
        if (recentTransactions.isEmpty()) {
            return false;
        }

        // Calculate average of recent transactions
        BigDecimal totalAmount = recentTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageAmount = totalAmount.divide(
                new BigDecimal(recentTransactions.size()), 2, BigDecimal.ROUND_HALF_UP);

        // Check if current transaction is significantly higher than average
        BigDecimal threshold = averageAmount.multiply(SPIKE_MULTIPLIER);
        return currentTransaction.getAmount().compareTo(threshold) > 0;
    }

    private boolean hasRapidSuccessiveTransactions(List<Transaction> recentTransactions) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        long transactionsInLastHour = recentTransactions.stream()
                .filter(t -> t.getCreatedAt().isAfter(oneHourAgo))
                .count();

        return transactionsInLastHour >= MAX_TRANSACTIONS_PER_HOUR;
    }
}