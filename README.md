# Banking System API

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![JWT](https://img.shields.io/badge/Auth-JWT-purple?style=flat-square)
![Maven](https://img.shields.io/badge/Build-Maven-red?style=flat-square&logo=apachemaven)
![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)

A production-grade RESTful Banking System built with Java and Spring Boot, implementing real-world backend engineering concepts including ACID transactions, JWT authentication, concurrency control, role-based access control, and dashboard analytics.

---

## Features

- User registration and login with JWT-based stateless authentication
- Three-tier role system — ADMIN, ANALYST, VIEWER — with enforced access control
- User status management — ACTIVE and INACTIVE
- Bank account creation and management — Savings and Current accounts
- Fund transfers — Deposit, withdraw, and transfer with concurrency-safe logic
- UPI-style transfers — by account number, phone number, or IFSC code
- Full transaction CRUD with filtering by date, category, and type
- Dashboard summary APIs — total income, expenses, net balance, category totals, monthly trends
- Pessimistic locking to prevent race conditions in concurrent transfers
- Input validation and global exception handling with consistent error responses

---

## Assignment Requirements Coverage

| Requirement | Implementation | Status |
|---|---|---|
| User and Role Management | ADMIN, ANALYST, VIEWER roles with ACTIVE/INACTIVE status | ![Done](https://img.shields.io/badge/-Done-brightgreen?style=flat-square) |
| Financial Records Management | Full CRUD with category field and dynamic filtering | ![Done](https://img.shields.io/badge/-Done-brightgreen?style=flat-square) |
| Dashboard Summary APIs | Income, expenses, balance, category totals, monthly trends | ![Done](https://img.shields.io/badge/-Done-brightgreen?style=flat-square) |
| Access Control Logic | Spring Security hasRole restrictions per endpoint | ![Done](https://img.shields.io/badge/-Done-brightgreen?style=flat-square) |
| Validation and Error Handling | @Valid + GlobalExceptionHandler + proper HTTP codes | ![Done](https://img.shields.io/badge/-Done-brightgreen?style=flat-square) |
| Data Persistence | MySQL 8 with Spring Data JPA and Hibernate | ![Done](https://img.shields.io/badge/-Done-brightgreen?style=flat-square) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT |
| Database | MySQL 8 |
| ORM | Spring Data JPA + Hibernate |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Architecture

The project follows a strict layered architecture to separate concerns:

```
HTTP Request
     |
     v
Controller     Handles HTTP requests, delegates to service layer
     |
     v
Service        All business logic and rules live here
     |
     v
Repository     Talks to the database via JPA
     |
     v
MySQL Database
```

---

## Database Schema

```
users
  id             PK
  full_name
  email          unique
  password       BCrypt hashed
  phone_number   unique
  role           ADMIN | ANALYST | VIEWER
  status         ACTIVE | INACTIVE
  created_at

accounts
  id             PK
  account_number unique
  account_type   SAVINGS | CURRENT
  balance        DECIMAL
  status         ACTIVE | FROZEN
  ifsc_code
  branch_address
  user_id        FK -> users
  created_at

transactions
  id               PK
  from_account_id  FK -> accounts (nullable for deposits)
  to_account_id    FK -> accounts
  amount           DECIMAL
  type             DEPOSIT | WITHDRAWAL | TRANSFER
  category
  description
  created_at
```

---

## Role Permissions

| Action | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| View dashboard and transactions | ![Yes](https://img.shields.io/badge/-Yes-brightgreen?style=flat-square) | ![Yes](https://img.shields.io/badge/-Yes-brightgreen?style=flat-square) | ![Yes](https://img.shields.io/badge/-Yes-brightgreen?style=flat-square) |
| Create transactions | ![No](https://img.shields.io/badge/-No-red?style=flat-square) | ![Yes](https://img.shields.io/badge/-Yes-brightgreen?style=flat-square) | ![Yes](https://img.shields.io/badge/-Yes-brightgreen?style=flat-square) |
| Delete transactions | ![No](https://img.shields.io/badge/-No-red?style=flat-square) | ![No](https://img.shields.io/badge/-No-red?style=flat-square) | ![Yes](https://img.shields.io/badge/-Yes-brightgreen?style=flat-square) |
| Manage users and accounts | ![No](https://img.shields.io/badge/-No-red?style=flat-square) | ![No](https://img.shields.io/badge/-No-red?style=flat-square) | ![Yes](https://img.shields.io/badge/-Yes-brightgreen?style=flat-square) |

---

## Getting Started

### Prerequisites

- Java 17 or higher
- MySQL 8 or higher
- Maven

### Setup

```bash
# Clone the repository
git clone https://github.com/dipanwitapaul578/bankingSystem.git
cd bankingSystem

# Create the database
mysql -u root -p
CREATE DATABASE banking_system;

# Configure credentials
cp application.properties.example src/main/resources/application.properties
# Edit application.properties with your MySQL credentials

# Run the application
mvn spring-boot:run
```

The application runs on `http://localhost:8080`

---

## API Endpoints

### Authentication — Public

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT token |

### Accounts

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/accounts` | Create a bank account | All |
| `GET` | `/api/accounts/{accountNumber}` | Get account details | All |

### Transactions

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/transactions/deposit/{accountNumber}` | Deposit funds | ANALYST, ADMIN |
| `POST` | `/api/transactions/withdraw/{accountNumber}` | Withdraw funds | ANALYST, ADMIN |
| `POST` | `/api/transactions/transfer` | Transfer funds | ANALYST, ADMIN |
| `GET` | `/api/transactions/history/{accountNumber}` | Filtered history | All |
| `PUT` | `/api/transactions/{id}` | Update transaction | ANALYST, ADMIN |
| `DELETE` | `/api/transactions/{id}` | Delete transaction | ADMIN |

### Dashboard

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/api/dashboard/{accountNumber}` | Income, expenses, trends | All |

### Admin

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/api/admin/users` | Get all users | ADMIN |
| `GET` | `/api/admin/accounts` | Get all accounts | ADMIN |
| `GET` | `/api/admin/transactions` | Get all transactions | ADMIN |
| `PUT` | `/api/admin/accounts/{accountNumber}/freeze` | Freeze account | ADMIN |
| `PUT` | `/api/admin/accounts/{accountNumber}/unfreeze` | Unfreeze account | ADMIN |

---

## Project Structure

```
src/main/java/com/bank/
    controller/     REST controllers
    service/        Business logic
    repository/     Database access layer
    model/          JPA entities
    dto/            Request and response DTOs
    exception/      Global exception handling
    config/         Security, JWT, and CORS configuration
```

---

## CS Concepts Implemented

| Concept | Where |
|---|---|
| ACID Transactions | Fund transfers with @Transactional |
| Pessimistic Locking | Concurrent transfer safety |
| BCrypt Hashing | Password storage |
| Stateless Authentication | JWT tokens |
| Layered Architecture | Controller, Service, Repository separation |
| DTO Pattern | Entities never exposed directly in API responses |
| Foreign Key Relationships | Account to User, Transaction to Account |
| Role-Based Access Control | ADMIN, ANALYST, VIEWER permissions |
| Aggregated Queries | Dashboard summary calculations |
| Dynamic Filtering | Transaction history with date, type, category filters |

---

## Roadmap

- [x] Project setup and dependencies
- [x] Database schema and JPA models
- [x] User registration with BCrypt password hashing
- [x] JWT authentication and security filter chain
- [x] Account creation and management
- [x] Deposit, withdrawal, and fund transfer APIs
- [x] UPI-style transfers by account number, phone, and IFSC code
- [x] Transaction CRUD with filtering
- [x] Global exception handling
- [x] Role-based access control — ADMIN, ANALYST, VIEWER
- [x] User status management — ACTIVE, INACTIVE
- [x] Dashboard summary APIs
- [x] Admin APIs for user and account management
- [ ] Unit tests with JUnit and Mockito
- [ ] Docker setup
- [ ] Swagger documentation

---

## Author

**Dipanwita Paul**

[![GitHub](https://img.shields.io/badge/GitHub-dipanwitapaul578-black?style=flat-square&logo=github)](https://github.com/dipanwitapaul578)
