# SecureBank Testing Strategy

## Overview
Comprehensive testing approach using Mockito for unit tests and TestContainers for integration tests, ensuring high code quality and reliability.

## Test Structure

```
src/test/java/com/securebank/
├── service/                    # Unit Tests with Mockito
│   ├── TransactionServiceTest.java
│   ├── AccountServiceTest.java
│   ├── AuthServiceTest.java
│   └── FraudDetectionServiceTest.java
└── integration/               # Integration Tests with TestContainers
    └── TransactionIntegrationTest.java
```

## Unit Tests with Mockito

### TransactionServiceTest
**Coverage**: All transaction operations with comprehensive edge cases

#### Test Scenarios:
- ✅ **Deposit Operations**
  - Successful deposit with balance update
  - Account not found exception
  - Frozen account exception
  - Access control validation

- ✅ **Withdrawal Operations**
  - Successful withdrawal with fee calculation
  - Insufficient balance exception
  - Minimum balance violation
  - Daily limit exceeded exception
  - Account ownership validation

- ✅ **Transfer Operations**
  - Successful transfer between accounts
  - Self-transfer prevention
  - Destination account not found
  - Frozen destination account
  - Concurrent transfer handling

- ✅ **Security & Access Control**
  - User ownership validation
  - Transaction retrieval by ID
  - Access denied for unauthorized users

### AccountServiceTest
**Coverage**: Account management operations

#### Test Scenarios:
- ✅ Account creation with validation
- ✅ User account retrieval
- ✅ Account balance queries
- ✅ Access control enforcement
- ✅ Error handling for invalid requests

### AuthServiceTest
**Coverage**: Authentication and authorization

#### Test Scenarios:
- ✅ User registration with validation
- ✅ Duplicate username/email handling
- ✅ Login with JWT token generation
- ✅ Invalid credentials handling
- ✅ Password encryption verification

### FraudDetectionServiceTest
**Coverage**: Suspicious transaction detection

#### Test Scenarios:
- ✅ Multiple large transactions detection
- ✅ Unusual hour transactions (11 PM - 6 AM)
- ✅ Sudden amount spike detection
- ✅ Rapid successive transactions
- ✅ Weekend large transaction patterns
- ✅ Normal transaction validation

## Integration Tests with TestContainers

### TransactionIntegrationTest
**Coverage**: End-to-end transaction flows with real database

#### Test Scenarios:
- ✅ **Real Database Operations**
  - PostgreSQL container setup
  - Actual database transactions
  - Data persistence validation

- ✅ **Concurrency Testing**
  - Multiple simultaneous transfers
  - Race condition handling
  - Transaction isolation verification
  - Deadlock prevention

- ✅ **ACID Properties**
  - Atomicity: All-or-nothing transactions
  - Consistency: Data integrity maintenance
  - Isolation: Concurrent transaction separation
  - Durability: Persistent data storage

## Key Testing Features

### 1. Mockito Annotations
```java
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private TransactionService transactionService;
}
```

### 2. TestContainers Setup
```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
    .withDatabaseName("securebank_test")
    .withUsername("test")
    .withPassword("test");
```

### 3. Comprehensive Assertions
```java
// Balance verification
assertEquals(new BigDecimal("1450.00"), finalSource.getBalance());

// Exception testing
assertThrows(InsufficientBalanceException.class, 
    () -> transactionService.withdraw(request, 1L));

// Mock verification
verify(accountRepository).save(any(Account.class));
verify(transactionRepository, times(2)).save(any());
```

## Business Logic Testing

### Transaction Validation
- ✅ Amount validation (positive values)
- ✅ Account status verification (ACTIVE only)
- ✅ Balance sufficiency checks
- ✅ Daily limit enforcement
- ✅ Minimum balance maintenance

### Fee Calculations
- ✅ Withdrawal fee: ₹5 per transaction
- ✅ Transfer fee: ₹10 per transaction
- ✅ Deposit fee: ₹0 (free)
- ✅ Total deduction calculations

### Security Validations
- ✅ User ownership verification
- ✅ Account access control
- ✅ Transaction authorization
- ✅ Audit log creation

## Fraud Detection Testing

### Pattern Recognition
```java
@Test
void shouldDetectSuspiciousMultipleLargeTransactions() {
    // Multiple ₹10,000+ transactions within 1 hour
    // Should flag as suspicious
}

@Test
void shouldDetectSuspiciousUnusualHours() {
    // Large transactions between 11 PM - 6 AM
    // Should flag as suspicious
}
```

### Risk Factors
- ✅ Transaction amount thresholds
- ✅ Time-based patterns
- ✅ Frequency analysis
- ✅ Historical comparison

## Running Tests

### Command Line
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TransactionServiceTest

# Run with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest=*IntegrationTest
```

### Using Test Script
```bash
# Windows
run-tests.bat

# Results in:
# - target/surefire-reports/ (Unit test results)
# - target/site/jacoco/ (Coverage reports)
```

## Test Coverage Metrics

### Target Coverage
- **Service Layer**: 95%+ line coverage
- **Controller Layer**: 90%+ line coverage
- **Repository Layer**: 85%+ line coverage
- **Overall Project**: 90%+ line coverage

### Coverage Reports
- **HTML Report**: `target/site/jacoco/index.html`
- **XML Report**: `target/site/jacoco/jacoco.xml`
- **CSV Report**: `target/site/jacoco/jacoco.csv`

## Best Practices Implemented

### 1. Test Organization
- Clear test method naming
- Arrange-Act-Assert pattern
- Comprehensive setup methods
- Proper test isolation

### 2. Mock Usage
- Minimal mocking (only external dependencies)
- Verification of interactions
- Argument matchers for flexibility
- Proper mock lifecycle management

### 3. Data Management
- Test data builders
- Realistic test scenarios
- Edge case coverage
- Boundary value testing

### 4. Error Testing
- Exception scenario coverage
- Error message validation
- Rollback verification
- Failure recovery testing

## Continuous Integration

### Pre-commit Hooks
- All tests must pass
- Coverage thresholds enforced
- Code quality checks
- Security vulnerability scans

### Build Pipeline
1. Unit tests execution
2. Integration tests with TestContainers
3. Coverage report generation
4. Quality gate validation
5. Deployment approval

## Professional Testing Maturity

This testing strategy demonstrates:
- **Comprehensive Coverage**: All critical paths tested
- **Real-world Scenarios**: Concurrency, failures, edge cases
- **Professional Tools**: Mockito, TestContainers, JaCoCo
- **Maintainable Tests**: Clear structure, good practices
- **CI/CD Ready**: Automated execution and reporting