package com.securebank.service;

import com.securebank.entity.Transaction;
import com.securebank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;

    private static final BigDecimal LARGE_AMOUNT_THRESHOLD = new BigDecimal("5000");
    private static final BigDecimal SPIKE_MULTIPLIER = new BigDecimal("10");
    private static final int MAX_TRANSACTIONS_PER_HOUR = 5;
    private static final int UNUSUAL_HOUR_START = 23;
    private static final int UNUSUAL_HOUR_END = 6;

    public boolean isSuspicious(Transaction transaction) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        List<Transaction> recentTransactions =
                transactionRepository.findRecentTransactionsByAccount(
                        transaction.getFromAccount().getId(), oneDayAgo);

        if (hasMultipleLargeTransactions(recentTransactions, transaction)) {
            return true;
        }

        if (isUnusualHour(transaction) && isLargeAmount(transaction.getAmount())) {
            return true;
        }

        if (isLargeWeekendTransaction(transaction)) {
            return true;
        }

        if (hasSuddenAmountSpike(recentTransactions, transaction)) {
            return true;
        }

        return hasRapidSuccessiveTransactions(recentTransactions);
    }

    private boolean hasMultipleLargeTransactions(
            List<Transaction> recentTransactions,
            Transaction currentTransaction) {

        long largeTransactionCount = recentTransactions.stream()
                .filter(t -> isLargeAmount(t.getAmount()))
                .count();

        if (isLargeAmount(currentTransaction.getAmount())) {
            largeTransactionCount++;
        }

        return largeTransactionCount >= 3;
    }

    private boolean isUnusualHour(Transaction transaction) {
        int hour = transaction.getCreatedAt().getHour();
        return hour >= UNUSUAL_HOUR_START || hour <= UNUSUAL_HOUR_END;
    }

    private boolean isLargeAmount(BigDecimal amount) {
        return amount.compareTo(LARGE_AMOUNT_THRESHOLD) >= 0;
    }

    private boolean isLargeWeekendTransaction(Transaction transaction) {
        DayOfWeek day = transaction.getCreatedAt().getDayOfWeek();

        boolean isWeekend =
                day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;

        return isWeekend && isLargeAmount(transaction.getAmount());
    }

    private boolean hasSuddenAmountSpike(
            List<Transaction> recentTransactions,
            Transaction currentTransaction) {

        if (recentTransactions.isEmpty()) {
            return false;
        }

        BigDecimal totalAmount = recentTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageAmount = totalAmount.divide(
                BigDecimal.valueOf(recentTransactions.size()),
                2,
                RoundingMode.HALF_UP);

        BigDecimal threshold = averageAmount.multiply(SPIKE_MULTIPLIER);

        return currentTransaction.getAmount().compareTo(threshold) > 0;
    }

    private boolean hasRapidSuccessiveTransactions(
            List<Transaction> recentTransactions) {

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        long transactionsInLastHour = recentTransactions.stream()
                .filter(t -> t.getCreatedAt().isAfter(oneHourAgo))
                .count();

        return transactionsInLastHour >= MAX_TRANSACTIONS_PER_HOUR;
    }
}