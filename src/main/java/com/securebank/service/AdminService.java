package com.securebank.service;

import com.securebank.dto.response.AdminDashboardResponse;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.entity.Account;
import com.securebank.entity.AuditLog;
import com.securebank.entity.Transaction;
import com.securebank.enums.AccountStatus;
import com.securebank.enums.TransactionStatus;
import com.securebank.enums.TransactionType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.AuditLogRepository;
import com.securebank.repository.TransactionRepository;
import com.securebank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminDashboardResponse getDashboardMetrics() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        LocalDate weekAgo = today.minusDays(7);

        // Basic counts
        Long totalUsers = userRepository.count();
        Long totalAccounts = accountRepository.count();
        Long newUsersThisWeek = userRepository.countByCreatedAtAfter(weekAgo.atStartOfDay());

        // Today's transaction metrics
        BigDecimal todayVolume = getTodayTransactionVolume(startOfDay, endOfDay);
        Long todayCount = getTodayTransactionCount(startOfDay, endOfDay);

        // Account status distribution
        Long activeAccounts = accountRepository.countByStatus(AccountStatus.ACTIVE);
        Long frozenAccounts = accountRepository.countByStatus(AccountStatus.FROZEN);
        Long closedAccounts = accountRepository.countByStatus(AccountStatus.CLOSED);

        // Transaction type breakdown for today
        BigDecimal todayDeposits = getTodayTransactionsByType(TransactionType.DEPOSIT, startOfDay, endOfDay);
        BigDecimal todayWithdrawals = getTodayTransactionsByType(TransactionType.WITHDRAW, startOfDay, endOfDay);
        BigDecimal todayTransfers = getTodayTransactionsByType(TransactionType.TRANSFER, startOfDay, endOfDay);

        // Transaction counts by type
        Long depositCount = getTodayTransactionCountByType(TransactionType.DEPOSIT, startOfDay, endOfDay);
        Long withdrawalCount = getTodayTransactionCountByType(TransactionType.WITHDRAW, startOfDay, endOfDay);
        Long transferCount = getTodayTransactionCountByType(TransactionType.TRANSFER, startOfDay, endOfDay);

        // Daily volume for last 7 days
        Map<String, BigDecimal> dailyVolume = getDailyVolumeForLastWeek();

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalAccounts(totalAccounts)
                .newUsersThisWeek(newUsersThisWeek)
                .todayTransactionVolume(todayVolume)
                .todayTransactionCount(todayCount)
                .activeAccounts(activeAccounts)
                .frozenAccounts(frozenAccounts)
                .closedAccounts(closedAccounts)
                .todayDeposits(todayDeposits)
                .todayWithdrawals(todayWithdrawals)
                .todayTransfers(todayTransfers)
                .depositCount(depositCount)
                .withdrawalCount(withdrawalCount)
                .transferCount(transferCount)
                .dailyVolume(dailyVolume)
                .build();
    }

    private BigDecimal getTodayTransactionVolume(LocalDateTime start, LocalDateTime end) {
        BigDecimal volume = transactionRepository.sumTransactionVolumeByDateRange(start, end);
        return volume != null ? volume : BigDecimal.ZERO;
    }

    private Long getTodayTransactionCount(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.countTransactionsByDateRange(start, end);
    }

    private BigDecimal getTodayTransactionsByType(TransactionType type, LocalDateTime start, LocalDateTime end) {
        BigDecimal amount = transactionRepository.sumTransactionVolumeByTypeAndDateRange(type, start, end);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private Long getTodayTransactionCountByType(TransactionType type, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.countTransactionsByTypeAndDateRange(type, start, end);
    }

    private Map<String, BigDecimal> getDailyVolumeForLastWeek() {
        Map<String, BigDecimal> dailyVolume = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            
            BigDecimal volume = getTodayTransactionVolume(start, end);
            dailyVolume.put(date.toString(), volume);
        }
        
        return dailyVolume;
    }

    @Transactional
    public void freezeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
        
        // Create audit log
        AuditLog auditLog = AuditLog.builder()
                .user(account.getUser())
                .action("ACCOUNT_FROZEN")
                .entityType("ACCOUNT")
                .entityId(accountId)
                .build();
        auditLogRepository.save(auditLog);
    }

    @Transactional
    public void unfreezeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        
        // Create audit log
        AuditLog auditLog = AuditLog.builder()
                .user(account.getUser())
                .action("ACCOUNT_UNFROZEN")
                .entityType("ACCOUNT")
                .entityId(accountId)
                .build();
        auditLogRepository.save(auditLog);
    }

    public Page<TransactionResponse> getAllTransactions(
            LocalDateTime startDate,
            LocalDateTime endDate,
            TransactionType type,
            TransactionStatus status,
            String username,
            String accountNumber,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable) {
        
        Page<Transaction> transactions = transactionRepository.findAllTransactionsWithFilters(
                startDate, endDate, type, status, username, accountNumber, minAmount, maxAmount, pageable);
        
        return transactions.map(this::mapToTransactionResponse);
    }

    public Object getAllUsers() {
        return userRepository.findAll();
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionId(transaction.getTransactionId());
        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setFee(transaction.getFee());
        response.setDescription(transaction.getDescription());
        response.setFromAccountNumber(transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNumber() : null);
        response.setToAccountNumber(transaction.getToAccount() != null ? transaction.getToAccount().getAccountNumber() : null);
        response.setStatus(transaction.getStatus());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}