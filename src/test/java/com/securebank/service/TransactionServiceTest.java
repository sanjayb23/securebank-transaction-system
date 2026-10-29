package com.securebank.service;

import com.securebank.dto.request.DepositRequest;
import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.request.WithdrawRequest;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.entity.Account;
import com.securebank.entity.Transaction;
import com.securebank.entity.User;
import com.securebank.enums.AccountStatus;
import com.securebank.enums.AccountType;
import com.securebank.enums.TransactionStatus;
import com.securebank.enums.TransactionType;
import com.securebank.exception.AccountFrozenException;
import com.securebank.exception.DailyLimitExceededException;
import com.securebank.exception.InsufficientBalanceException;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.AuditLogRepository;
import com.securebank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Account sourceAccount;
    private Account destinationAccount;
    private Transaction mockTransaction;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                transactionService, "dailyLimit", new BigDecimal("50000"));

        ReflectionTestUtils.setField(
                transactionService, "minBalance", new BigDecimal("500"));

        ReflectionTestUtils.setField(
                transactionService, "transferFee", new BigDecimal("10"));

        ReflectionTestUtils.setField(
                transactionService, "withdrawFee", new BigDecimal("5"));

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        sourceAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC001")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("1000"))
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();

        destinationAccount = Account.builder()
                .id(2L)
                .accountNumber("ACC002")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("500"))
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();

        mockTransaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN123")
                .referenceId("REF-TEST-001")
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100"))
                .fee(BigDecimal.ZERO)
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    // DEPOSIT TESTS

    @Test
    void shouldDepositSuccessfully() {
        when(accountRepository.findByIdWithLock(1L))
            .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.save(any(Account.class)))
                .thenReturn(sourceAccount);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(mockTransaction);

        when(auditLogRepository.save(any()))
                .thenReturn(null);

        DepositRequest request = new DepositRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100"));
        request.setReferenceId("REF-DEPOSIT-001");
        request.setDescription("Test deposit");

        TransactionResponse response =
                transactionService.deposit(request, 1L);

        assertNotNull(response);
        assertEquals(
                new BigDecimal("1100"),
                sourceAccount.getBalance());

        verify(accountRepository).save(sourceAccount);
        verify(transactionRepository).save(any(Transaction.class));
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDepositAccountNotFound() {
        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.empty());
                
        DepositRequest request = new DepositRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100"));
        request.setReferenceId("REF-DEPOSIT-002");

        assertThrows(
                RuntimeException.class,
                () -> transactionService.deposit(request, 1L));

        verify(accountRepository, never()).save(any());
    }

    @Test
        void shouldThrowExceptionWhenDepositAccountFrozen() {
        sourceAccount.setStatus(AccountStatus.FROZEN);

        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        DepositRequest request = new DepositRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100"));
        request.setReferenceId("REF-DEPOSIT-003");

        assertThrows(
                AccountFrozenException.class,
                () -> transactionService.deposit(request, 1L)
        );

        verify(accountRepository, never()).save(any());
}

    // WITHDRAW TESTS

    @Test
    void shouldWithdrawSuccessfully() {
        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(sourceAccount);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(mockTransaction);

        when(transactionRepository.sumDailyWithdrawals(
                anyLong(), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        when(auditLogRepository.save(any()))
                .thenReturn(null);

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100"));
        request.setReferenceId("REF-WITHDRAW-001");
        request.setDescription("Test withdrawal");

        TransactionResponse response =
                transactionService.withdraw(request, 1L);

        assertNotNull(response);
        assertEquals(
                new BigDecimal("895"),
                sourceAccount.getBalance());

        verify(accountRepository).save(sourceAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        sourceAccount.setBalance(new BigDecimal("50"));

        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100"));
        request.setReferenceId("REF-WITHDRAW-002");

        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.withdraw(request, 1L));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMinimumBalanceViolation() {
        sourceAccount.setBalance(new BigDecimal("600"));

        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100"));
        request.setReferenceId("REF-WITHDRAW-003");

        assertThrows(
                RuntimeException.class,
                () -> transactionService.withdraw(request, 1L));
    }

    @Test
    void shouldThrowExceptionWhenDailyLimitExceeded() {
        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(transactionRepository.sumDailyWithdrawals(
                anyLong(), any(LocalDate.class)))
                .thenReturn(new BigDecimal("49900"));

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("200"));
        request.setReferenceId("REF-WITHDRAW-004");

        assertThrows(
                DailyLimitExceededException.class,
                () -> transactionService.withdraw(request, 1L));
    }

    // TRANSFER TESTS

    @Test
    void shouldTransferMoneySuccessfully() {
        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByAccountNumberWithLock("ACC002"))
                .thenReturn(Optional.of(destinationAccount));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(mockTransaction);

        when(transactionRepository.sumDailyTransfers(
                anyLong(), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        when(auditLogRepository.save(any()))
                .thenReturn(null);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("ACC002");
        request.setAmount(new BigDecimal("200"));
        request.setReferenceId("REF-TRANSFER-001");
        request.setDescription("Test transfer");

        TransactionResponse response =
                transactionService.transfer(request, 1L);

        assertNotNull(response);
        assertEquals(
                new BigDecimal("790"),
                sourceAccount.getBalance());

        assertEquals(
                new BigDecimal("700"),
                destinationAccount.getBalance());

        verify(accountRepository, times(2))
                .save(any(Account.class));

        verify(transactionRepository)
                .save(any(Transaction.class));

        verify(auditLogRepository, times(2))
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenTransferToSameAccount() {
        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByAccountNumberWithLock("ACC001"))
                .thenReturn(Optional.of(sourceAccount));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("ACC001");
        request.setAmount(new BigDecimal("200"));
        request.setReferenceId("REF-TRANSFER-002");

        assertThrows(
                RuntimeException.class,
                () -> transactionService.transfer(request, 1L));
    }

    @Test
    void shouldThrowExceptionWhenDestinationAccountNotFound() {
        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByAccountNumberWithLock("INVALID"))
                .thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("INVALID");
        request.setAmount(new BigDecimal("200"));
        request.setReferenceId("REF-TRANSFER-003");

        assertThrows(
                RuntimeException.class,
                () -> transactionService.transfer(request, 1L));
    }

    @Test
    void shouldThrowExceptionWhenDestinationAccountFrozen() {
        destinationAccount.setStatus(AccountStatus.FROZEN);

        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByAccountNumberWithLock("ACC002"))
                .thenReturn(Optional.of(destinationAccount));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("ACC002");
        request.setAmount(new BigDecimal("200"));
        request.setReferenceId("REF-TRANSFER-004");

        assertThrows(
                AccountFrozenException.class,
                () -> transactionService.transfer(request, 1L));
    }

    // ACCESS CONTROL TESTS

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnAccount() {
        when(accountRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(sourceAccount));

        DepositRequest request = new DepositRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100"));
        request.setReferenceId("REF-DEPOSIT-004");

        assertThrows(
                RuntimeException.class,
                () -> transactionService.deposit(request, 999L));
    }

    // TRANSACTION RETRIEVAL TESTS

    @Test
    void shouldGetTransactionById() {
        mockTransaction.setFromAccount(sourceAccount);

        when(transactionRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.of(mockTransaction));

        TransactionResponse response =
                transactionService.getTransactionById("TXN123", 1L);

        assertNotNull(response);
        assertEquals("TXN123", response.getTransactionId());
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        when(transactionRepository.findByTransactionId("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> transactionService.getTransactionById("INVALID", 1L));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnTransaction() {
        User otherUser = User.builder()
                .id(999L)
                .username("other")
                .build();

        Account otherAccount = Account.builder()
                .id(999L)
                .user(otherUser)
                .build();

        mockTransaction.setFromAccount(otherAccount);

        when(transactionRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.of(mockTransaction));

        assertThrows(
                RuntimeException.class,
                () -> transactionService.getTransactionById("TXN123", 1L));
    }
}