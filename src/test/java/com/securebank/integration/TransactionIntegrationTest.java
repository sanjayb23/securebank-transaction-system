package com.securebank.integration;

import com.securebank.dto.request.DepositRequest;
import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.request.WithdrawRequest;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.entity.Account;
import com.securebank.entity.User;
import com.securebank.enums.AccountStatus;
import com.securebank.enums.AccountType;
import com.securebank.enums.UserRole;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.UserRepository;
import com.securebank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "jwt.secret=ThisIsATestSecretKeyForSecureBank123456789",
    "jwt.expiration=86400000"
}) 

@Testcontainers
class TransactionIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("securebank_test")
            .withUsername("test")
            .withPassword("test");

   @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                postgres.getJdbcUrl() + "?options=-c%20TimeZone%3DUTC");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.datasource.driver-class-name",
            () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform",
            () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto",
            () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.jdbc.time_zone",
            () -> "UTC");
    }

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User testUser;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
	String testId = UUID.randomUUID().toString().substring(0, 8);
        testUser = User.builder()
                .username("testuser" + testId)
                .email("test" + testId + "@example.com")
                .fullName("Test User")
                .password("password")
                .role(UserRole.USER)
                .build();
        testUser = userRepository.save(testUser);

        sourceAccount = Account.builder()
                .accountNumber("ACC001" + testId)
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("2000"))
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
        sourceAccount = accountRepository.save(sourceAccount);

        destinationAccount = Account.builder()
                .accountNumber("ACC002" + testId)
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("1000"))
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
        destinationAccount = accountRepository.save(destinationAccount);
    }

    @Test
    void shouldPerformDepositSuccessfully() {
        DepositRequest request = new DepositRequest();
        request.setAccountId(sourceAccount.getId());
        request.setAmount(new BigDecimal("500"));
        request.setDescription("Integration test deposit");
	request.setReferenceId("DEPOSIT-TEST-001");

        TransactionResponse response = transactionService.deposit(request, testUser.getId());

        assertNotNull(response);
        assertNotNull(response.getTransactionId());
        assertEquals(new BigDecimal("500"), response.getAmount());

        Account updatedAccount = accountRepository.findById(sourceAccount.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("2500.00").compareTo(updatedAccount.getBalance()));
    }

    @Test
    void shouldHandleConcurrentTransfers() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        CompletableFuture<TransactionResponse>[] futures = new CompletableFuture[3];
        
        for (int i = 0; i < 3; i++) {
            final int index = i;
            futures[i] = CompletableFuture.supplyAsync(() -> {
                TransferRequest request = new TransferRequest();
                request.setFromAccountId(sourceAccount.getId());
                request.setToAccountNumber(destinationAccount.getAccountNumber());
                request.setAmount(new BigDecimal("100"));
		request.setDescription("Concurrent transfer " + index);
		request.setReferenceId("TRANSFER-TEST-" + index);                
                return transactionService.transfer(request, testUser.getId());
            }, executor);
        }

        CompletableFuture.allOf(futures).join();

        for (CompletableFuture<TransactionResponse> future : futures) {
            TransactionResponse response = future.get();
            assertNotNull(response);
            assertEquals(new BigDecimal("100"), response.getAmount());
        }

        Account finalSource = accountRepository.findById(sourceAccount.getId()).orElseThrow();
        Account finalDestination = accountRepository.findById(destinationAccount.getId()).orElseThrow();

        assertEquals(new BigDecimal("1670.00"), finalSource.getBalance()); // 2000 - 300 - 30 fee
        assertEquals(new BigDecimal("1300.00"), finalDestination.getBalance()); // 1000 + 300

        executor.shutdown();
    } 
}
