package com.securebank.service;

import com.securebank.entity.Account;
import com.securebank.entity.Transaction;
import com.securebank.entity.User;
import com.securebank.enums.TransactionType;
import com.securebank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    private User testUser;
    private Account testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC001")
                .user(testUser)
                .build();

        testTransaction = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.WITHDRAW)
                .amount(new BigDecimal("1000"))
                .fromAccount(testAccount)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldDetectSuspiciousMultipleLargeTransactions() {
        // Arrange - Multiple large transactions in short time
        List<Transaction> recentTransactions = Arrays.asList(
                createTransaction(new BigDecimal("10000"), LocalDateTime.now().minusMinutes(5)),
                createTransaction(new BigDecimal("15000"), LocalDateTime.now().minusMinutes(10)),
                createTransaction(new BigDecimal("12000"), LocalDateTime.now().minusMinutes(15))
        );
        
        when(transactionRepository.findRecentTransactionsByAccount(anyLong(), any(LocalDateTime.class)))
                .thenReturn(recentTransactions);

        // Act
        boolean isSuspicious = fraudDetectionService.isSuspicious(testTransaction);

        // Assert
        assertTrue(isSuspicious, "Should detect multiple large transactions as suspicious");
    }

    @Test
    void shouldDetectSuspiciousUnusualHours() {
        // Arrange - Transaction at 3 AM
        testTransaction.setCreatedAt(LocalDateTime.now().withHour(3).withMinute(0));
        testTransaction.setAmount(new BigDecimal("5000"));
        
        when(transactionRepository.findRecentTransactionsByAccount(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        // Act
        boolean isSuspicious = fraudDetectionService.isSuspicious(testTransaction);

        // Assert
        assertTrue(isSuspicious, "Should detect unusual hour transactions as suspicious");
    }

    @Test
    void shouldDetectSuspiciousSuddenSpike() {
        // Arrange - Normal small transactions, then sudden large one
        List<Transaction> recentTransactions = Arrays.asList(
                createTransaction(new BigDecimal("100"), LocalDateTime.now().minusDays(1)),
                createTransaction(new BigDecimal("200"), LocalDateTime.now().minusDays(2)),
                createTransaction(new BigDecimal("150"), LocalDateTime.now().minusDays(3))
        );
        
        testTransaction.setAmount(new BigDecimal("20000")); // Sudden spike
        
        when(transactionRepository.findRecentTransactionsByAccount(anyLong(), any(LocalDateTime.class)))
                .thenReturn(recentTransactions);

        // Act
        boolean isSuspicious = fraudDetectionService.isSuspicious(testTransaction);

        // Assert
        assertTrue(isSuspicious, "Should detect sudden amount spike as suspicious");
    }

    @Test
    void shouldNotFlagNormalTransaction() {
        // Arrange - Normal transaction during business hours
        testTransaction.setAmount(new BigDecimal("500"));
        testTransaction.setCreatedAt(LocalDateTime.now().withHour(14).withMinute(30)); // 2:30 PM
        
        List<Transaction> recentTransactions = Arrays.asList(
                createTransaction(new BigDecimal("400"), LocalDateTime.now().minusDays(1)),
                createTransaction(new BigDecimal("600"), LocalDateTime.now().minusDays(2))
        );
        
        when(transactionRepository.findRecentTransactionsByAccount(anyLong(), any(LocalDateTime.class)))
                .thenReturn(recentTransactions);

        // Act
        boolean isSuspicious = fraudDetectionService.isSuspicious(testTransaction);

        // Assert
        assertFalse(isSuspicious, "Should not flag normal transactions as suspicious");
    }

    @Test
    void shouldDetectRapidSuccessiveTransactions() {
        // Arrange - Many transactions in very short time
        List<Transaction> rapidTransactions = Arrays.asList(
                createTransaction(new BigDecimal("1000"), LocalDateTime.now().minusMinutes(1)),
                createTransaction(new BigDecimal("1000"), LocalDateTime.now().minusMinutes(2)),
                createTransaction(new BigDecimal("1000"), LocalDateTime.now().minusMinutes(3)),
                createTransaction(new BigDecimal("1000"), LocalDateTime.now().minusMinutes(4)),
                createTransaction(new BigDecimal("1000"), LocalDateTime.now().minusMinutes(5))
        );
        
        when(transactionRepository.findRecentTransactionsByAccount(anyLong(), any(LocalDateTime.class)))
                .thenReturn(rapidTransactions);

        // Act
        boolean isSuspicious = fraudDetectionService.isSuspicious(testTransaction);

        // Assert
        assertTrue(isSuspicious, "Should detect rapid successive transactions as suspicious");
    }

    @Test
    void shouldDetectWeekendLargeTransactions() {
        // Arrange - Large transaction on weekend
        testTransaction.setAmount(new BigDecimal("8000"));
        testTransaction.setCreatedAt(LocalDateTime.now().withHour(22).withMinute(0)); // 10 PM weekend
        
        when(transactionRepository.findRecentTransactionsByAccount(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        // Act
        boolean isSuspicious = fraudDetectionService.isSuspicious(testTransaction);

        // Assert
        assertTrue(isSuspicious, "Should detect large weekend transactions as suspicious");
    }

    private Transaction createTransaction(BigDecimal amount, LocalDateTime createdAt) {
        return Transaction.builder()
                .amount(amount)
                .fromAccount(testAccount)
                .transactionType(TransactionType.WITHDRAW)
                .createdAt(createdAt)
                .build();
    }
}