package com.securebank.service;

import com.securebank.dto.request.CreateAccountRequest;
import com.securebank.dto.response.AccountResponse;
import com.securebank.entity.Account;
import com.securebank.entity.User;
import com.securebank.enums.AccountStatus;
import com.securebank.enums.AccountType;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .build();

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC001")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("1000"))
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        // Act
        AccountResponse response = accountService.createAccount(request, 1L);

        // Assert
        assertNotNull(response);
        verify(accountRepository).save(any(Account.class));
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        // Act & Assert
        assertThrows(RuntimeException.class, 
                () -> accountService.createAccount(request, 1L));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldGetUserAccounts() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findByUserId(1L)).thenReturn(accounts);

        // Act
        List<AccountResponse> responses = accountService.getUserAccounts(1L);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(accountRepository).findByUserId(1L);
    }

    @Test
    void shouldGetAccountById() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        AccountResponse response = accountService.getAccountById(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals("ACC001", response.getAccountNumber());
        verify(accountRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
                () -> accountService.getAccountById(1L, 1L));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnAccount() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(RuntimeException.class, 
                () -> accountService.getAccountById(1L, 999L)); // Different user
    }

    @Test
    void shouldGetAccountBalance() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        BigDecimal balance = accountService.getAccountBalance(1L, 1L);

        // Assert
        assertEquals(new BigDecimal("1000"), balance);
        verify(accountRepository).findById(1L);
    }
}