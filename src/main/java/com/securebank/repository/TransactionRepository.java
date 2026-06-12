package com.securebank.repository;

import com.securebank.entity.Account;
import com.securebank.entity.Transaction;
import com.securebank.enums.TransactionStatus;
import com.securebank.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    Page<Transaction> findByFromAccountOrToAccount(Account fromAccount, Account toAccount, Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount.id = :accountId AND t.transactionType = 'WITHDRAW' AND DATE(t.createdAt) = :date AND t.status = 'COMPLETED'")
    BigDecimal sumDailyWithdrawals(@Param("accountId") Long accountId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount.id = :accountId AND t.transactionType = 'TRANSFER' AND DATE(t.createdAt) = :date AND t.status = 'COMPLETED'")
    BigDecimal sumDailyTransfers(@Param("accountId") Long accountId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED'")
    BigDecimal sumTransactionVolumeByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED'")
    Long countTransactionsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = :type AND t.createdAt BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED'")
    BigDecimal sumTransactionVolumeByTypeAndDateRange(@Param("type") TransactionType type, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionType = :type AND t.createdAt BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED'")
    Long countTransactionsByTypeAndDateRange(@Param("type") TransactionType type, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId) " +
           "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
           "AND (:type IS NULL OR t.transactionType = :type) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findUserTransactionsWithFilters(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate) AND " +
           "(:type IS NULL OR t.transactionType = :type) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:username IS NULL OR t.fromAccount.user.username LIKE %:username% OR t.toAccount.user.username LIKE %:username%) AND " +
           "(:accountNumber IS NULL OR t.fromAccount.accountNumber = :accountNumber OR t.toAccount.accountNumber = :accountNumber) AND " +
           "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR t.amount <= :maxAmount) " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findAllTransactionsWithFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("username") String username,
            @Param("accountNumber") String accountNumber,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable);

    List<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE (t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId) AND t.createdAt >= :thirtyDaysAgo")
    Long countByUserIdInLast30Days(@Param("userId") Long userId, @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE (t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId) AND t.status = 'PENDING'")
    Long countPendingByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId AND t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactionsByAccount(@Param("accountId") Long accountId, @Param("since") LocalDateTime since);
}