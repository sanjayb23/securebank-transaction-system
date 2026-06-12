# SecureBank - Complete Banking Transaction System

A full-stack production-grade banking application with Spring Boot backend and React frontend, featuring real-time transactions, JWT authentication, comprehensive admin controls, and complete audit logging.

## Resume Highlights

- Built a secure banking transaction platform supporting deposits, withdrawals, and account transfers.
- Implemented JWT authentication and Role-Based Access Control (RBAC).
- Designed audit logging and transaction monitoring for administrative oversight.
- Developed RESTful APIs using Spring Boot with PostgreSQL persistence.
- Containerized the application using Docker and Docker Compose.

## Screenshots

### Login Page
![Login](assets/login.png)

### Dashboard
![Dashboard](assets/dashboard.png)

### Account Management
![Accounts](assets/accounts.png)

### Transactions
![Transactions](assets/transactions.png)

### Admin Dashboard
![Admin](assets/admin.png)

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
┌─────────────────────────────────────────┐
│              React Frontend             │
│ Authentication • Dashboard              │
│ Accounts • Transactions                 │
│ Admin Panel                             │
└───────────────────┬─────────────────────┘
                    │ HTTPS / JWT
                    ▼
┌─────────────────────────────────────────┐
│            Spring Boot Backend          │
├─────────────────────────────────────────┤
│ Authentication Service                  │
│ Account Management Service              │
│ Transaction Processing Service          │
│ Fraud Detection Service                 │
│ Audit Logging Service                   │
│ Admin Management Service                │
└───────────────────┬─────────────────────┘
                    │ JPA / Hibernate
                    ▼
┌─────────────────────────────────────────┐
│              PostgreSQL DB              │
├─────────────────────────────────────────┤
│ Users                                   │
│ Accounts                                │
│ Transactions                            │
│ Audit Logs                              │
└─────────────────────────────────────────┘
```

## 📂 Project Structure

```text
securebank-transaction-system/
│
├── securebank-frontend/          # React Frontend
│   ├── src/
│   │   ├── components/           # Reusable UI Components
│   │   ├── pages/                # Application Pages
│   │   ├── services/             # API Service Layer
│   │   ├── hooks/                # Custom React Hooks
│   │   ├── context/              # Authentication Context
│   │   └── utils/                # Utility Functions
│   ├── public/
│   └── package.json
│
├── src/main/java/
│   └── com/securebank/
│       ├── controller/           # REST API Controllers
│       ├── service/              # Business Logic Layer
│       ├── repository/           # Database Access Layer
│       ├── entity/               # JPA Entities
│       ├── dto/                  # Request/Response DTOs
│       ├── config/               # Security & App Config
│       ├── exception/            # Global Exception Handling
│       └── util/                 # Helper Utilities
│
├── src/test/java/                # Unit & Integration Tests
├── testsprite_tests/             # API Test Suite
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```


## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- JWT
- Maven

### Frontend
- React
- Vite
- Tailwind CSS
- Axios
- React Query

### Database
- PostgreSQL

### DevOps
- Docker
- Docker Compose

## 🚀 Features

### User Features
- Account Management
- Deposits & Withdrawals
- Account Transfers
- Transaction History

### Admin Features
- User Management
- Transaction Monitoring
- Audit Logs

### Security Features
- JWT Authentication
- RBAC
- BCrypt Password Hashing
- Fraud Detection

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
git clone https://github.com/sanjayb23/securebank-transaction-system.git
cd securebank-transaction-system
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

## 🔒 Security Features

- JWT Authentication
- Role-Based Access Control (RBAC)
- BCrypt Password Hashing
- Input Validation & SQL Injection Protection
- Audit Logging
- Fraud Detection
- Secure REST APIs

  
## 🧪 Testing

The project includes automated unit, integration, and API tests to ensure reliability and correctness across core banking operations.

### Backend Tests

Run all backend tests:

```bash
mvn test
```

Run integration tests:

```bash
mvn verify
```

### API Tests

Run the API test suite:

```bash
cd testsprite_tests
python -m pytest -v
```

### Test Coverage

- Authentication & Authorization
- Account Management
- Deposits & Withdrawals
- Account Transfers
- Transaction Validation
- Fraud Detection
- Audit Logging
- REST API Endpoints
