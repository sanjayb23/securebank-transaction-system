# SecureBank - Complete Banking Transaction System

A full-stack production-grade banking application with Spring Boot backend and React frontend, featuring real-time transactions, JWT authentication, comprehensive admin controls, and complete audit logging.

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security Features](#security-features)
- [Business Rules](#business-rules)
- [Testing](#testing)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 🎯 Project Overview

SecureBank is a comprehensive banking transaction system designed to handle real-world banking operations with enterprise-grade security, scalability, and reliability. The system supports multiple account types, real-time transactions, fraud detection, and complete audit trails.

### Key Capabilities
- **Multi-user Banking**: Support for individual and business accounts
- **Real-time Transactions**: Instant deposits, withdrawals, and transfers
- **Admin Dashboard**: Complete administrative control and monitoring
- **Fraud Detection**: Built-in fraud detection and prevention
- **Audit Logging**: Complete transaction and user activity tracking
- **Mobile-first Design**: Responsive UI for all devices

## 🏗️ Architecture

### System Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend │    │  Spring Boot    │    │   PostgreSQL    │
│   (Port 3000)   │◄──►│   Backend       │◄──►│   Database      │
│                 │    │   (Port 8080)   │    │   (Port 5432)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Project Structure
```
Banking Transaction API System/
├── securebank-frontend/          # React Frontend Application
│   ├── src/
│   │   ├── components/           # Reusable UI Components
│   │   │   ├── accounts/         # Account-related components
│   │   │   │   ├── AccountCard.jsx
│   │   │   │   ├── AccountDetails.jsx
│   │   │   │   ├── AccountStatement.jsx
│   │   │   │   └── CreateAccountModal.jsx
│   │   │   ├── admin/            # Admin panel components
│   │   │   ├── auth/             # Authentication components
│   │   │   │   ├── LoginForm.jsx
│   │   │   │   ├── PrivateRoute.jsx
│   │   │   │   └── RegisterForm.jsx
│   │   │   ├── common/           # Common UI components
│   │   │   │   ├── Button.jsx
│   │   │   │   ├── Card.jsx
│   │   │   │   ├── Modal.jsx
│   │   │   │   └── LoadingSpinner.jsx
│   │   │   ├── layout/           # Layout components
│   │   │   │   ├── Header.jsx
│   │   │   │   ├── Navbar.jsx
│   │   │   │   └── Sidebar.jsx
│   │   │   └── transactions/     # Transaction components
│   │   │       ├── TransactionCard.jsx
│   │   │       ├── TransactionForm.jsx
│   │   │       └── TransactionHistory.jsx
│   │   ├── context/              # React Context
│   │   │   └── AuthContext.jsx
│   │   ├── hooks/                # Custom React Hooks
│   │   │   ├── useAccounts.js
│   │   │   ├── useAuth.jsx
│   │   │   └── useTransactions.js
│   │   ├── pages/                # Main Application Pages
│   │   │   ├── Dashboard.jsx
│   │   │   ├── Accounts.jsx
│   │   │   ├── Transactions.jsx
│   │   │   ├── AdminDashboard.jsx
│   │   │   └── Login.jsx
│   │   ├── services/             # API Service Layer
│   │   │   ├── api.js
│   │   │   ├── authService.js
│   │   │   ├── accountService.js
│   │   │   ├── transactionService.js
│   │   │   └── adminService.js
│   │   └── utils/                # Utility Functions
│   │       ├── formatters.js
│   │       └── validators.js
│   ├── public/                   # Static Assets
│   ├── package.json              # Frontend Dependencies
│   ├── vite.config.js           # Vite Configuration
│   ├── tailwind.config.js       # Tailwind CSS Config
│   └── Dockerfile               # Frontend Docker Config
├── src/main/java/               # Spring Boot Backend
│   └── com/securebank/
│       ├── config/              # Configuration Classes
│       │   ├── SecurityConfig.java
│       │   ├── JwtAuthFilter.java
│       │   ├── CorsConfig.java
│       │   └── SwaggerConfig.java
│       ├── controller/          # REST API Controllers
│       │   ├── AuthController.java
│       │   ├── AccountController.java
│       │   ├── TransactionController.java
│       │   ├── AdminController.java
│       │   └── HealthController.java
│       ├── service/             # Business Logic Services
│       │   ├── AuthService.java
│       │   ├── AccountService.java
│       │   ├── TransactionService.java
│       │   ├── AdminService.java
│       │   ├── JwtService.java
│       │   ├── FraudDetectionService.java
│       │   └── UserDetailsServiceImpl.java
│       ├── repository/          # Data Access Layer
│       │   ├── UserRepository.java
│       │   ├── AccountRepository.java
│       │   ├── TransactionRepository.java
│       │   └── AuditLogRepository.java
│       ├── entity/              # JPA Entities
│       │   ├── User.java
│       │   ├── Account.java
│       │   ├── Transaction.java
│       │   └── AuditLog.java
│       ├── dto/                 # Data Transfer Objects
│       │   ├── request/         # Request DTOs
│       │   │   ├── LoginRequest.java
│       │   │   ├── RegisterRequest.java
│       │   │   ├── CreateAccountRequest.java
│       │   │   ├── DepositRequest.java
│       │   │   ├── WithdrawRequest.java
│       │   │   └── TransferRequest.java
│       │   └── response/        # Response DTOs
│       │       ├── AuthResponse.java
│       │       ├── UserResponse.java
│       │       ├── AccountResponse.java
│       │       ├── TransactionResponse.java
│       │       └── ApiResponse.java
│       ├── enums/               # Enumeration Classes
│       │   ├── UserRole.java
│       │   ├── AccountType.java
│       │   ├── AccountStatus.java
│       │   ├── TransactionType.java
│       │   └── TransactionStatus.java
│       ├── exception/           # Exception Handling
│       │   ├── GlobalExceptionHandler.java
│       │   ├── InsufficientBalanceException.java
│       │   ├── AccountFrozenException.java
│       │   └── DailyLimitExceededException.java
│       └── util/                # Utility Classes
│           └── AccountNumberGenerator.java
├── src/test/java/               # Test Classes
│   └── com/securebank/
│       ├── service/             # Service Tests
│       │   ├── AuthServiceTest.java
│       │   ├── AccountServiceTest.java
│       │   ├── TransactionServiceTest.java
│       │   └── FraudDetectionServiceTest.java
│       └── integration/         # Integration Tests
│           └── TransactionIntegrationTest.java
├── testsprite_tests/            # API Test Suite
│   ├── TC001_test_user_registration_api.py
│   ├── TC002_test_user_login_api.py
│   ├── TC003_test_get_current_user_api.py
│   ├── TC004_test_create_account_api.py
│   ├── TC005_test_get_accounts_api.py
│   ├── TC006_test_get_account_by_id_api.py
│   ├── TC007_test_get_account_balance_api.py
│   ├── TC008_test_get_account_statement_api.py
│   ├── TC009_test_update_account_status_api.py
│   ├── TC010_test_deposit_money_api.py
│   ├── testsprite_backend_test_plan.json
│   └── testsprite_frontend_test_plan.json
├── docker-compose.yml           # Docker Compose Configuration
├── Dockerfile                   # Backend Docker Configuration
├── pom.xml                     # Maven Dependencies
├── lombok.config               # Lombok Configuration
└── application.properties      # Application Configuration
```

## 🛠️ Tech Stack

### Frontend Technologies
- **React 18.2.0** - Modern React with Hooks and Context
- **Vite 4.1.0** - Fast build tool and development server
- **Tailwind CSS 3.2.0** - Utility-first CSS framework
- **React Router DOM 6.8.0** - Client-side routing
- **Axios 1.3.0** - HTTP client for API calls
- **TanStack React Query 4.24.0** - Data fetching and caching
- **Lucide React 0.263.0** - Modern icon library
- **Recharts 2.5.0** - Charts and data visualization
- **Date-fns 2.29.0** - Date manipulation library

### Backend Technologies
- **Java 17** - Latest LTS version of Java
- **Spring Boot 3.2.0** - Enterprise application framework
- **Spring Security 6.x** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **Spring Web** - RESTful web services
- **Spring Validation** - Input validation
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Lombok 1.18.30** - Boilerplate code reduction
- **Maven 3.8+** - Dependency management and build tool

### Database & Infrastructure
- **PostgreSQL 15** - Primary database with ACID compliance
- **Docker & Docker Compose** - Containerization and orchestration
- **Swagger/OpenAPI 3** - API documentation
- **JaCoCo** - Code coverage reporting
- **TestContainers** - Integration testing with real databases

### Development & Testing Tools
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for tests
- **TestSprite** - API testing suite
- **Python pytest** - API test automation
- **Postman/Insomnia** - API testing and documentation

## 🚀 Features

### Frontend Features
- **Real-time Dashboard**
  - Live balance updates across all accounts
  - Transaction count and status indicators
  - Pending transaction notifications
  - Quick action buttons for common operations

- **Account Management**
  - Create multiple account types (Savings, Current)
  - View detailed account information
  - Account balance tracking
  - Account statement generation with pagination
  - Account status management (Active, Frozen, Closed)

- **Transaction Operations**
  - Instant deposits with real-time balance updates
  - Secure withdrawals with balance validation
  - Inter-account transfers with fee calculation
  - Transaction history with advanced filtering
  - Transaction search and export capabilities

- **User Interface**
  - Mobile-first responsive design
  - Dark/Light theme support
  - Intuitive navigation with breadcrumbs
  - Real-time notifications and alerts
  - Progressive Web App (PWA) capabilities

- **Admin Panel**
  - User management and account oversight
  - Transaction monitoring and reporting
  - System health and performance metrics
  - Audit log viewing and analysis
  - Bulk operations and data export

### Backend Features
- **ACID Transactions**
  - Atomic money transfers with rollback capability
  - Pessimistic locking for concurrent operations
  - Transaction isolation and consistency
  - Deadlock detection and resolution

- **Security & Authentication**
  - JWT-based stateless authentication
  - Role-based access control (USER, ADMIN)
  - Password encryption with BCrypt
  - Session management and token refresh
  - CORS configuration for cross-origin requests

- **Business Logic**
  - Daily transaction limits enforcement
  - Minimum balance requirements
  - Transaction fee calculations
  - Account type-specific rules
  - Fraud detection algorithms

- **Audit & Compliance**
  - Complete transaction audit trails
  - User activity logging
  - Admin action tracking
  - Compliance reporting
  - Data retention policies

- **API Features**
  - RESTful API design
  - Comprehensive error handling
  - Input validation and sanitization
  - Rate limiting and throttling
  - API versioning support

## 📋 Prerequisites

### System Requirements
- **Java Development Kit (JDK) 17+**
- **Node.js 18+ and npm**
- **Docker Desktop** (recommended for easy setup)
- **PostgreSQL 15+** (if running without Docker)
- **Maven 3.8+** (for manual builds)

### Development Tools (Optional)
- **IntelliJ IDEA** or **VS Code** for development
- **pgAdmin** or **DBeaver** for database management
- **Postman** or **Insomnia** for API testing
- **Git** for version control

## 🚀 Installation & Setup

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
```bash
git clone <repository-url>
cd "Banking Transaction API System"
```

2. **Start all services**
```bash
docker-compose up --build
```

3. **Access the applications**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Database: 127.0.0.1:5432 (postgres/postgres)

### Option 2: Manual Setup

#### Backend Setup
1. **Start PostgreSQL**
```bash
docker run -d --name postgres -p 5432:5432 \
  -e POSTGRES_DB=securebank \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:15
```

2. **Build and run backend**
```bash
# Using Maven (if installed)
mvn clean install
mvn spring-boot:run

# Or using Docker
docker build -t securebank-backend .
docker run -p 8080:8080 securebank-backend
```

#### Frontend Setup
```bash
cd securebank-frontend
npm install
npm run dev
```

### Option 3: Development Setup

1. **Backend Development**
```bash
# Install dependencies
mvn clean install

# Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test
mvn verify
```

2. **Frontend Development**
```bash
cd securebank-frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build
```

## ⚙️ Configuration

### Backend Configuration (application.properties)
```properties
# Application Settings
spring.application.name=securebank-api
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/securebank
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=bXlzdXBlcnNlY3JldGtleWZvcmp3dHRva2VuZ2VuZXJhdGlvbm1pbjI1NmJpdHM=
jwt.expiration=86400000

# Transaction Limits
transaction.daily.limit=50000
transaction.minimum.balance=500
transaction.transfer.fee=10

# Swagger Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Logging
logging.level.com.securebank=DEBUG
```

### Frontend Configuration (.env)
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### Docker Configuration
```yaml
# docker-compose.yml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: securebank
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/securebank

  frontend:
    build: ./securebank-frontend
    ports:
      - "3000:80"
    depends_on:
      - backend

volumes:
  postgres_data:
```

## 👤 Default Users

The system comes with pre-configured users for testing:

### Admin User
- **Username**: admin
- **Password**: admin123
- **Role**: ADMIN
- **Permissions**: 
  - Full system access
  - User management
  - Account administration
  - Transaction monitoring
  - System reports and analytics

### Test User
- **Username**: testuser
- **Password**: password123
- **Role**: USER
- **Permissions**:
  - Personal account management
  - Transaction operations
  - Account statements
  - Profile management

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/auth/register` | Register new user | `RegisterRequest` | `AuthResponse` |
| POST | `/api/auth/login` | User login | `LoginRequest` | `AuthResponse` |
| GET | `/api/auth/me` | Get current user | - | `UserResponse` |

### Account Management Endpoints

| Method | Endpoint | Description | Auth Required | Request Body |
|--------|----------|-------------|---------------|--------------|
| POST | `/api/accounts` | Create new account | Yes | `CreateAccountRequest` |
| GET | `/api/accounts` | Get user accounts | Yes | - |
| GET | `/api/accounts/{id}` | Get account details | Yes | - |
| GET | `/api/accounts/{id}/balance` | Get account balance | Yes | - |
| GET | `/api/accounts/{id}/statement` | Get account statement | Yes | Query params |

### Transaction Endpoints

| Method | Endpoint | Description | Auth Required | Request Body |
|--------|----------|-------------|---------------|--------------|
| POST | `/api/transactions/deposit` | Deposit money | Yes | `DepositRequest` |
| POST | `/api/transactions/withdraw` | Withdraw money | Yes | `WithdrawRequest` |
| POST | `/api/transactions/transfer` | Transfer money | Yes | `TransferRequest` |
| GET | `/api/transactions` | Get user transactions | Yes | Query params |
| GET | `/api/transactions/{id}` | Get transaction details | Yes | - |

### Admin Endpoints (ADMIN Role Required)

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/api/admin/users` | Get all users | `List<UserResponse>` |
| GET | `/api/admin/accounts` | Get all accounts | `List<AccountResponse>` |
| GET | `/api/admin/transactions` | Get all transactions | `Page<TransactionResponse>` |
| POST | `/api/admin/accounts/{id}/freeze` | Freeze account | `ApiResponse` |
| POST | `/api/admin/accounts/{id}/unfreeze` | Unfreeze account | `ApiResponse` |
| GET | `/api/admin/reports/daily` | Get daily report | `AdminDashboardResponse` |
| GET | `/api/admin/audit-logs` | Get audit logs | `Page<AuditLog>` |

### Request/Response Examples

#### Register User
```json
POST /api/auth/register
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securePassword123",
  "fullName": "John Doe",
  "phone": "+1234567890"
}

Response:
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "username": "johndoe",
      "email": "john@example.com",
      "fullName": "John Doe",
      "role": "USER"
    }
  }
}
```

#### Create Account
```json
POST /api/accounts
Authorization: Bearer <token>
{
  "accountType": "SAVINGS",
  "currency": "INR"
}

Response:
{
  "success": true,
  "message": "Account created successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1234567890",
    "accountType": "SAVINGS",
    "balance": 0.00,
    "currency": "INR",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

#### Transfer Money
```json
POST /api/transactions/transfer
Authorization: Bearer <token>
{
  "fromAccountId": 1,
  "toAccountNumber": "ACC0987654321",
  "amount": 1000.00,
  "description": "Payment for services"
}

Response:
{
  "success": true,
  "message": "Transfer completed successfully",
  "data": {
    "id": 1,
    "transactionId": "TXN1234567890",
    "transactionType": "TRANSFER",
    "amount": 1000.00,
    "fee": 10.00,
    "description": "Payment for services",
    "fromAccountNumber": "ACC1234567890",
    "toAccountNumber": "ACC0987654321",
    "status": "COMPLETED",
    "createdAt": "2024-01-01T10:30:00"
  }
}
```

## 💾 Database Schema

### Entity Relationship Diagram
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    Users    │    │  Accounts   │    │Transactions │    │ Audit_Logs  │
├─────────────┤    ├─────────────┤    ├─────────────┤    ├─────────────┤
│ id (PK)     │◄──┐│ id (PK)     │◄──┐│ id (PK)     │    │ id (PK)     │
│ username    │   ││ account_num │   ││ trans_id    │    │ user_id (FK)│
│ email       │   ││ account_type│   ││ trans_type  │    │ action      │
│ password    │   ││ balance     │   ││ amount      │    │ entity_type │
│ full_name   │   ││ currency    │   ││ fee         │    │ entity_id   │
│ phone       │   ││ status      │   ││ description │    │ ip_address  │
│ role        │   ││ user_id (FK)│   ││ from_acc_id │    │ timestamp   │
│ is_verified │   ││ created_at  │   ││ to_acc_id   │    └─────────────┘
│ created_at  │   ││ updated_at  │   ││ status      │
│ updated_at  │   │└─────────────┘   ││ created_at  │
└─────────────┘   └──────────────────┘└─────────────┘
```

### Core Tables

#### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
```

#### Accounts Table
```sql
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('SAVINGS', 'CURRENT')),
    balance DECIMAL(15,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'INR',
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_balance CHECK (balance >= 0),
    CONSTRAINT valid_currency CHECK (currency IN ('INR', 'USD', 'EUR', 'GBP'))
);

-- Indexes for performance
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_accounts_status ON accounts(status);
```

#### Transactions Table
```sql
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    amount DECIMAL(15,2) NOT NULL,
    fee DECIMAL(10,2) DEFAULT 0.00,
    description TEXT,
    from_account_id BIGINT REFERENCES accounts(id),
    to_account_id BIGINT REFERENCES accounts(id),
    status VARCHAR(20) DEFAULT 'COMPLETED' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_amount CHECK (amount > 0),
    CONSTRAINT valid_accounts CHECK (
        (transaction_type = 'DEPOSIT' AND from_account_id IS NULL AND to_account_id IS NOT NULL) OR
        (transaction_type = 'WITHDRAWAL' AND from_account_id IS NOT NULL AND to_account_id IS NULL) OR
        (transaction_type = 'TRANSFER' AND from_account_id IS NOT NULL AND to_account_id IS NOT NULL AND from_account_id != to_account_id)
    )
);

-- Indexes for performance
CREATE INDEX idx_transactions_from_account ON transactions(from_account_id);
CREATE INDEX idx_transactions_to_account ON transactions(to_account_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_type ON transactions(transaction_type);
```

#### Audit Logs Table
```sql
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
```

### Database Views

#### User Account Summary View
```sql
CREATE VIEW user_account_summary AS
SELECT 
    u.id as user_id,
    u.username,
    u.full_name,
    COUNT(a.id) as total_accounts,
    SUM(a.balance) as total_balance,
    COUNT(CASE WHEN a.status = 'ACTIVE' THEN 1 END) as active_accounts,
    COUNT(CASE WHEN a.status = 'FROZEN' THEN 1 END) as frozen_accounts
FROM users u
LEFT JOIN accounts a ON u.id = a.user_id
GROUP BY u.id, u.username, u.full_name;
```

#### Transaction Summary View
```sql
CREATE VIEW transaction_summary AS
SELECT 
    DATE(created_at) as transaction_date,
    transaction_type,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount,
    SUM(fee) as total_fees,
    AVG(amount) as average_amount
FROM transactions
WHERE status = 'COMPLETED'
GROUP BY DATE(created_at), transaction_type
ORDER BY transaction_date DESC;
```

## 🔒 Security Features

### Authentication & Authorization
- **JWT (JSON Web Tokens)**
  - Stateless authentication mechanism
  - 24-hour token expiry with refresh capability
  - Secure token signing with HMAC SHA-256
  - Token blacklisting for logout functionality

- **Password Security**
  - BCrypt hashing with configurable strength (12 rounds)
  - Password complexity requirements
  - Account lockout after failed attempts
  - Password history to prevent reuse

- **Role-Based Access Control (RBAC)**
  - USER role: Personal banking operations
  - ADMIN role: System administration and monitoring
  - Fine-grained permissions for each endpoint
  - Dynamic role assignment and management

### Data Protection
- **Input Validation**
  - Comprehensive request validation using Bean Validation
  - SQL injection prevention through parameterized queries
  - XSS protection with input sanitization
  - CSRF protection for state-changing operations

- **Encryption & Hashing**
  - Sensitive data encryption at rest
  - TLS/SSL encryption in transit
  - Database connection encryption
  - API key and secret management

- **Security Headers**
  - Content Security Policy (CSP)
  - X-Frame-Options for clickjacking protection
  - X-Content-Type-Options for MIME sniffing protection
  - Strict-Transport-Security for HTTPS enforcement

### Audit & Monitoring
- **Complete Audit Trail**
  - All user actions logged with timestamps
  - Transaction history with immutable records
  - Admin action tracking and approval workflows
  - Failed login attempt monitoring

- **Fraud Detection**
  - Unusual transaction pattern detection
  - Velocity checks for rapid transactions
  - Geographic anomaly detection
  - Machine learning-based risk scoring

## 🎯 Business Rules

### Account Management Rules
- **Account Creation**
  - Minimum age requirement: 18 years
  - Valid government ID verification
  - Initial deposit requirements vary by account type
  - Maximum 5 accounts per user

- **Account Types & Features**
  - **Savings Account**
    - Minimum balance: ₹500
    - Monthly transaction limit: 10 free transactions
    - Interest rate: 4% per annum
    - Overdraft: Not allowed
  
  - **Current Account**
    - Minimum balance: ₹10,000
    - Unlimited transactions
    - No interest on balance
    - Overdraft facility available

### Transaction Rules
- **Daily Limits**
  - Individual transaction limit: ₹50,000
  - Daily cumulative limit: ₹100,000
  - Monthly limit: ₹500,000
  - Limits can be increased with verification

- **Transaction Fees**
  - Transfer fee: ₹10 per transaction
  - Withdrawal fee: ₹5 per ATM withdrawal
  - International transfer: 2% of amount + ₹100
  - Same bank transfers: Free

- **Processing Times**
  - Same bank transfers: Instant
  - Other bank transfers: 2-4 hours
  - International transfers: 1-3 business days
  - Weekend processing: Limited services

### Compliance & Regulatory
- **KYC (Know Your Customer)**
  - Identity verification mandatory
  - Address proof required
  - Income verification for high-value accounts
  - Periodic KYC updates required

- **AML (Anti-Money Laundering)**
  - Transaction monitoring for suspicious patterns
  - Automatic reporting of large transactions
  - Customer due diligence procedures
  - Sanctions list screening

## 🧪 Testing

### Test Coverage Overview
- **Unit Tests**: 85%+ code coverage
- **Integration Tests**: All critical paths covered
- **API Tests**: Complete endpoint testing
- **End-to-End Tests**: User journey validation

### Backend Testing

#### Unit Tests
```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=TransactionServiceTest

# Run tests with coverage
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

#### Integration Tests
```bash
# Run integration tests
mvn verify

# Run with test containers
mvn verify -Dspring.profiles.active=test

# Run specific integration test
mvn verify -Dit.test=TransactionIntegrationTest
```

#### Test Classes
- **AuthServiceTest.java**: Authentication and authorization testing
- **AccountServiceTest.java**: Account management operations
- **TransactionServiceTest.java**: Transaction processing logic
- **FraudDetectionServiceTest.java**: Fraud detection algorithms
- **TransactionIntegrationTest.java**: End-to-end transaction flows

### Frontend Testing
```bash
cd securebank-frontend

# Run unit tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with coverage
npm test -- --coverage

# Run end-to-end tests
npm run test:e2e
```

### API Testing Suite

The project includes a comprehensive API testing suite using Python and pytest:

#### Test Cases
1. **TC001**: User Registration API
2. **TC002**: User Login API
3. **TC003**: Get Current User API
4. **TC004**: Create Account API
5. **TC005**: Get Accounts API
6. **TC006**: Get Account by ID API
7. **TC007**: Get Account Balance API
8. **TC008**: Get Account Statement API
9. **TC009**: Update Account Status API
10. **TC010**: Deposit Money API

#### Running API Tests
```bash
cd testsprite_tests

# Install dependencies
pip install -r requirements.txt

# Run all tests
python -m pytest -v

# Run specific test
python -m pytest TC001_test_user_registration_api.py -v

# Generate test report
python -m pytest --html=report.html --self-contained-html
```

### Performance Testing
- **Load Testing**: JMeter scripts for concurrent user simulation
- **Stress Testing**: Database connection pool and memory usage
- **Benchmark Testing**: API response time measurements
- **Scalability Testing**: Horizontal scaling validation

## 🚀 Deployment

### Production Deployment

#### Docker Production Setup
```bash
# Build production images
docker-compose -f docker-compose.prod.yml build

# Deploy to production
docker-compose -f docker-compose.prod.yml up -d

# Monitor logs
docker-compose logs -f

# Scale services
docker-compose up -d --scale backend=3
```

#### Environment Configuration
```bash
# Production environment variables
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=prod-db-server
export DB_PORT=5432
export DB_NAME=securebank_prod
export DB_USER=securebank_user
export DB_PASSWORD=secure_password
export JWT_SECRET=production_jwt_secret_key
export CORS_ALLOWED_ORIGINS=https://securebank.com
```

### Cloud Deployment Options

#### AWS Deployment
- **ECS/Fargate**: Container orchestration
- **RDS PostgreSQL**: Managed database service
- **Application Load Balancer**: Traffic distribution
- **CloudFront**: CDN for frontend assets
- **Route 53**: DNS management
- **Certificate Manager**: SSL/TLS certificates

#### Azure Deployment
- **Container Instances**: Serverless containers
- **Azure Database for PostgreSQL**: Managed database
- **Application Gateway**: Load balancing
- **CDN**: Content delivery network
- **DNS Zone**: Domain management

#### Google Cloud Deployment
- **Cloud Run**: Serverless container platform
- **Cloud SQL**: Managed PostgreSQL
- **Cloud Load Balancing**: Traffic management
- **Cloud CDN**: Global content delivery
- **Cloud DNS**: Domain name system

### CI/CD Pipeline

#### GitHub Actions Workflow
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run tests
        run: mvn clean test
      - name: Generate coverage report
        run: mvn jacoco:report
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3

  build-and-deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3
      - name: Build Docker images
        run: docker-compose build
      - name: Deploy to production
        run: |
          # Deployment script here
```

### Monitoring & Observability

#### Application Monitoring
- **Spring Boot Actuator**: Health checks and metrics
- **Micrometer**: Application metrics collection
- **Prometheus**: Metrics storage and alerting
- **Grafana**: Metrics visualization and dashboards

#### Logging
- **Structured Logging**: JSON format for log aggregation
- **ELK Stack**: Elasticsearch, Logstash, and Kibana
- **Log Levels**: Configurable logging levels per environment
- **Audit Logging**: Separate audit trail for compliance

#### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connectivity
curl http://localhost:8080/actuator/health/db

# Custom health indicators
curl http://localhost:8080/actuator/health/custom
```

## 🔧 Troubleshooting

### Common Issues & Solutions

#### Database Connection Issues
```bash
# Check database status
docker ps | grep postgres

# Check database logs
docker logs <postgres_container_id>

# Connect to database manually
docker exec -it <postgres_container> psql -U postgres -d securebank

# Reset database
docker-compose down -v
docker-compose up -d postgres
```

#### Application Startup Issues
```bash
# Check application logs
docker logs <backend_container_id>

# Verify environment variables
docker exec <backend_container> env | grep SPRING

# Check port availability
netstat -tulpn | grep :8080

# Restart services
docker-compose restart backend
```

#### Frontend Build Issues
```bash
# Clear node modules and reinstall
cd securebank-frontend
rm -rf node_modules package-lock.json
npm install

# Check for dependency conflicts
npm audit
npm audit fix

# Build with verbose output
npm run build -- --verbose
```

#### JWT Token Issues
- **Token Expired**: Implement token refresh mechanism
- **Invalid Signature**: Verify JWT secret configuration
- **Token Not Found**: Check Authorization header format
- **CORS Issues**: Configure allowed origins properly

#### Performance Issues
- **Slow Queries**: Add database indexes, optimize queries
- **Memory Leaks**: Monitor heap usage, implement connection pooling
- **High CPU**: Profile application, optimize algorithms
- **Network Latency**: Implement caching, use CDN

### Debug Mode
```bash
# Enable debug logging
export LOGGING_LEVEL_COM_SECUREBANK=DEBUG

# Run with debug profile
java -jar app.jar --spring.profiles.active=debug

# Remote debugging
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar
```

### Support & Maintenance

#### Regular Maintenance Tasks
- **Database Backup**: Daily automated backups
- **Log Rotation**: Weekly log archival
- **Security Updates**: Monthly dependency updates
- **Performance Monitoring**: Continuous monitoring and alerting

#### Emergency Procedures
- **Service Outage**: Incident response playbook
- **Data Breach**: Security incident response plan
- **Rollback Procedures**: Quick rollback to previous version
- **Disaster Recovery**: Backup restoration procedures

## 🤝 Contributing

### Development Workflow
1. **Fork** the repository
2. **Create** feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** changes (`git commit -m 'Add amazing feature'`)
4. **Push** to branch (`git push origin feature/amazing-feature`)
5. **Open** Pull Request

### Code Standards
- **Java**: Follow Google Java Style Guide
- **JavaScript**: Use ESLint and Prettier
- **Git Commits**: Use conventional commit messages
- **Documentation**: Update README for new features

### Pull Request Guidelines
- Include comprehensive tests for new features
- Update API documentation for endpoint changes
- Ensure all tests pass before submitting
- Add screenshots for UI changes
- Update CHANGELOG.md with notable changes

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot Team** for the excellent framework
- **React Team** for the powerful frontend library
- **PostgreSQL** for robust database features
- **Tailwind CSS** for beautiful styling system
- **TestContainers** for integration testing
- **Swagger** for API documentation
- **Docker** for containerization platform
- **Open Source Community** for continuous inspiration

## 📞 Support & Contact

### Getting Help
- **Documentation**: Check this README and inline code comments
- **Issues**: Create GitHub issue for bugs and feature requests
- **Discussions**: Use GitHub Discussions for questions
- **API Reference**: Visit `/swagger-ui.html` when running locally

### Project Maintainers
- **Backend Development**: Spring Boot, Security, Database
- **Frontend Development**: React, UI/UX, State Management
- **DevOps**: Docker, CI/CD, Deployment
- **Testing**: Unit Tests, Integration Tests, API Tests

### Roadmap
- [ ] Mobile application (React Native)
- [ ] Real-time notifications (WebSocket)
- [ ] Advanced analytics dashboard
- [ ] Multi-currency support
- [ ] Cryptocurrency integration
- [ ] AI-powered fraud detection
- [ ] Open Banking API compliance
- [ ] Microservices architecture migration

---

**Note**: This is a demonstration project showcasing modern banking API development. For production use, additional security measures, compliance features, regulatory approvals, and monitoring would be required.

**Version**: 1.0.0  
**Last Updated**: January 2024  
**Compatibility**: Java 17+, Node.js 18+, PostgreSQL 15+