package com.securebank.service;

import com.securebank.dto.request.DepositRequest;
import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.request.WithdrawRequest;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.exception.AccountFrozenException;
import com.securebank.exception.DailyLimitExceededException;
import com.securebank.exception.InsufficientBalanceException;
import com.securebank.entity.Account;
import com.securebank.entity.AuditLog;
import com.securebank.entity.Transaction;
import com.securebank.enums.AccountStatus;
import com.securebank.enums.TransactionStatus;
import com.securebank.enums.TransactionType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.AuditLogRepository;
import com.securebank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TransactionService {

    @Value("${transaction.daily.limit}")
    private BigDecimal dailyLimit;

    @Value("${transaction.minimum.balance}")
    private BigDecimal minBalance;

    @Value("${transaction.transfer.fee}")
    private BigDecimal transferFee;

    @Value("${transaction.withdraw.fee:5.00}")
    private BigDecimal withdrawFee;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final Random random = new Random();

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", random.nextInt(10000));
    }

    @Transactional
    public TransactionResponse deposit(DepositRequest request, Long userId) {
        Account account = getAccount(request.getAccountId(), userId);

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException(account.getAccountNumber());
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account not active");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .fee(BigDecimal.ZERO)
                .description(request.getDescription())
                .toAccount(account)
                .status(TransactionStatus.COMPLETED)
                .build();

        transaction = transactionRepository.save(transaction);

        // Audit log
        AuditLog auditLog = AuditLog.builder()
                .user(account.getUser())
                .action("DEPOSIT")
                .entityType("TRANSACTION")
                .entityId(transaction.getId())
                .build();
        auditLogRepository.save(auditLog);

        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request, Long userId) {
        Account account = accountRepository.findByIdWithLock(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException(account.getAccountNumber());
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account not active");
        }

        // Calculate total deduction (amount + fee)
        BigDecimal totalDeduction = request.getAmount().add(withdrawFee);
        
        // Check sufficient balance
        if (account.getBalance().compareTo(totalDeduction) < 0) {
            throw new InsufficientBalanceException(account.getBalance(), totalDeduction);
        }

        // Check minimum balance after withdrawal
        BigDecimal balanceAfterWithdraw = account.getBalance().subtract(totalDeduction);
        if (balanceAfterWithdraw.compareTo(minBalance) < 0) {
            throw new RuntimeException("Minimum balance violation");
        }

        // Check daily limit
        BigDecimal dailyTotal = transactionRepository.sumDailyWithdrawals(account.getId(), LocalDate.now());
        if (dailyTotal.add(request.getAmount()).compareTo(dailyLimit) > 0) {
            throw new DailyLimitExceededException(dailyTotal, dailyLimit, request.getAmount());
        }

        // Update balance
        account.setBalance(balanceAfterWithdraw);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .transactionType(TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .fee(withdrawFee)
                .description(request.getDescription())
                .fromAccount(account)
                .status(TransactionStatus.COMPLETED)
                .build();

        transaction = transactionRepository.save(transaction);

        // Audit log
        AuditLog auditLog = AuditLog.builder()
                .user(account.getUser())
                .action("WITHDRAW")
                .entityType("TRANSACTION")
                .entityId(transaction.getId())
                .build();
        auditLogRepository.save(auditLog);

        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request, Long userId) {
        // Lock source account first (prevent deadlocks by consistent ordering)
        Account fromAccount = accountRepository.findByIdWithLock(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        if (!fromAccount.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        if (fromAccount.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException(fromAccount.getAccountNumber());
        }
        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Source account not active");
        }

        // Lock destination account by account number
        Account toAccount = accountRepository.findByAccountNumberWithLock(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (toAccount.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException(toAccount.getAccountNumber());
        }
        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Destination account not active");
        }

        // Prevent self-transfer
        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        // Calculate total deduction
        BigDecimal totalDeduction = request.getAmount().add(transferFee);

        // Check sufficient balance
        if (fromAccount.getBalance().compareTo(totalDeduction) < 0) {
            throw new InsufficientBalanceException(fromAccount.getBalance(), totalDeduction);
        }

        // Check minimum balance after transfer
        BigDecimal balanceAfterTransfer = fromAccount.getBalance().subtract(totalDeduction);
        if (balanceAfterTransfer.compareTo(minBalance) < 0) {
            throw new RuntimeException("Minimum balance violation. Minimum required: ₹" + minBalance);
        }

        // Check daily transfer limit
        BigDecimal dailyTotal = transactionRepository.sumDailyTransfers(fromAccount.getId(), LocalDate.now());
        if (dailyTotal.add(request.getAmount()).compareTo(dailyLimit) > 0) {
            throw new DailyLimitExceededException(dailyTotal, dailyLimit, request.getAmount());
        }

        // Perform atomic balance updates
        fromAccount.setBalance(balanceAfterTransfer);
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .transactionType(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .fee(transferFee)
                .description(request.getDescription())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .status(TransactionStatus.COMPLETED)
                .build();

        transaction = transactionRepository.save(transaction);

        // Audit logs for both accounts
        AuditLog fromAuditLog = AuditLog.builder()
                .user(fromAccount.getUser())
                .action("TRANSFER_OUT")
                .entityType("TRANSACTION")
                .entityId(transaction.getId())
                .build();
        auditLogRepository.save(fromAuditLog);

        AuditLog toAuditLog = AuditLog.builder()
                .user(toAccount.getUser())
                .action("TRANSFER_IN")
                .entityType("TRANSACTION")
                .entityId(transaction.getId())
                .build();
        auditLogRepository.save(toAuditLog);

        return mapToResponse(transaction);
    }

    public Page<TransactionResponse> getTransactions(Long userId, Pageable pageable) {
        return getTransactionsWithFilters(userId, null, null, null, null, pageable);
    }

    public Page<TransactionResponse> getTransactionsWithFilters(
            Long userId, 
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            TransactionType type, 
            TransactionStatus status, 
            Pageable pageable) {
        
        Page<Transaction> transactions = transactionRepository.findUserTransactionsWithFilters(
                userId, startDate, endDate, type, status, pageable);
        
        return transactions.map(this::mapToResponse);
    }

    public TransactionResponse getTransactionById(String transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Check if user owns the account
        boolean ownsFrom = transaction.getFromAccount() != null && transaction.getFromAccount().getUser().getId().equals(userId);
        boolean ownsTo = transaction.getToAccount() != null && transaction.getToAccount().getUser().getId().equals(userId);

        if (!ownsFrom && !ownsTo) {
            throw new RuntimeException("Access denied");
        }

        return mapToResponse(transaction);
    }

    private Account getAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return account;
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
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