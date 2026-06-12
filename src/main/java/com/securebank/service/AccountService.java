package com.securebank.service;

import com.securebank.dto.request.CreateAccountRequest;
import com.securebank.dto.response.AccountResponse;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.entity.Account;
import com.securebank.entity.AuditLog;
import com.securebank.entity.Transaction;
import com.securebank.entity.User;
import com.securebank.enums.AccountStatus;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.AuditLogRepository;
import com.securebank.repository.TransactionRepository;
import com.securebank.repository.UserRepository;
import com.securebank.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountResponse createAccount(CreateAccountRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accountNumber = accountNumberGenerator.generate();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        account = accountRepository.save(account);

        // Audit log
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action("ACCOUNT_CREATED")
                .entityType("ACCOUNT")
                .entityId(account.getId())
                .build();
        auditLogRepository.save(auditLog);

        return mapToAccountResponse(account);
    }

    public List<AccountResponse> getAccountsByUser(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    public List<AccountResponse> getUserAccounts(long userId) {
        return getAccountsByUser(userId);
    }

    public AccountResponse getAccountById(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return mapToAccountResponse(account);
    }

    public BigDecimal getAccountBalance(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return account.getBalance();
    }

    public Page<TransactionResponse> getAccountStatement(Long accountId, Long userId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        Page<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(account, account, pageable);
        return transactions.map(this::mapToTransactionResponse);
    }

    @Transactional
    public void updateAccountStatus(Long accountId, AccountStatus status, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        account.setStatus(status);
        accountRepository.save(account);

        // Audit log
        User user = userRepository.findById(userId).orElse(null);
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action("ACCOUNT_STATUS_UPDATED")
                .entityType("ACCOUNT")
                .entityId(account.getId())
                .build();
        auditLogRepository.save(auditLog);
    }

    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }

    public Object getUserDashboard(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        Long recentTransactions = transactionRepository.countByUserIdInLast30Days(userId, thirtyDaysAgo);
        Long pendingTransactions = transactionRepository.countPendingByUserId(userId);
        
        return java.util.Map.of(
            "totalBalance", totalBalance,
            "totalAccounts", accounts.size(),
            "recentTransactions", recentTransactions,
            "pending", pendingTransactions
        );
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