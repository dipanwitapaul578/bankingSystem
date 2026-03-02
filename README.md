# 🏦 Banking System API

A production-grade **RESTful Banking System** built with **Java & Spring Boot**, implementing real-world backend engineering concepts including ACID transactions, JWT authentication, concurrency control, and role-based access.

> 🚧 **Currently in active development** — features being added weekly.

---

## 📌 Features

- 🔐 **User Authentication** — Register & login with JWT-based stateless auth
- 👤 **Role-Based Access Control** — Separate permissions for `ADMIN` and `CUSTOMER`
- 🏧 **Account Management** — Create and manage Savings & Current accounts
- 💸 **Fund Transfers** — Deposit, withdraw, and transfer with concurrency-safe logic
- 📜 **Transaction History** — Full audit trail of all account activity
- ⚡ **Pessimistic Locking** — Prevents race conditions during concurrent transfers
- ✅ **Input Validation** — Request validation with meaningful error messages
- 🛡️ **Global Exception Handling** — Consistent error responses across all endpoints

---

## 🛠️ Tech Stack

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

## 🏗️ Architecture

This project follows a strict **Layered Architecture** to separate concerns:

```
HTTP Request
     │
     ▼
┌─────────────┐
│  Controller │  ← Handles HTTP, delegates to service
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  ← All business logic lives here
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Repository  │  ← Talks to the database via JPA
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Database  │  ← MySQL
└─────────────┘
```

---

## 🗄️ Database Schema

```
users
 ├── id (PK)
 ├── full_name
 ├── email (unique)
 ├── password (bcrypt hashed)
 ├── phone_number (unique)
 ├── role (CUSTOMER | ADMIN)
 └── created_at

accounts
 ├── id (PK)
 ├── account_number (unique)
 ├── account_type (SAVINGS | CURRENT)
 ├── balance (DECIMAL)
 ├── status (ACTIVE | FROZEN)
 ├── user_id (FK → users)
 └── created_at

transactions
 ├── id (PK)
 ├── from_account_id (FK → accounts, nullable)
 ├── to_account_id (FK → accounts)
 ├── amount (DECIMAL)
 ├── type (DEPOSIT | WITHDRAWAL | TRANSFER)
 ├── description
 └── created_at
```

---

## 🔑 CS & System Design Concepts Implemented

| Concept | Where |
|---|---|
| ACID Transactions | Fund transfers with `@Transactional` |
| Pessimistic Locking | Concurrent transfer safety |
| BCrypt Hashing | Password storage |
| Stateless Auth | JWT tokens |
| Layered Architecture | Controller → Service → Repository |
| DB Normalization | Schema design |
| Foreign Key Relationships | Account ↔ User ↔ Transaction |
| Enum-based State | Account status, transaction type |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- MySQL 8+
- Maven

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/dipanwitapaul578/bankingSystem.git
cd bankingSystem

# 2. Create the database
mysql -u root -p
CREATE DATABASE banking_system;

# 3. Configure credentials
cp application.properties.example src/main/resources/application.properties
# Edit application.properties with your MySQL credentials

# 4. Run the application
mvn spring-boot:run
```

App runs on `http://localhost:8080`

---

## 📡 API Endpoints

> Full Swagger documentation coming soon

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login & get JWT | Public |
| POST | `/api/accounts` | Create bank account | Customer |
| GET | `/api/accounts/{id}` | Get account details | Customer |
| POST | `/api/accounts/{id}/deposit` | Deposit funds | Customer |
| POST | `/api/accounts/{id}/withdraw` | Withdraw funds | Customer |
| POST | `/api/transfer` | Transfer between accounts | Customer |
| GET | `/api/transactions/{accountId}` | Get transaction history | Customer |
| GET | `/api/admin/users` | Get all users | Admin |

---

## 📁 Project Structure

```
src/main/java/com/bank/
├── controller/       # REST controllers
├── service/          # Business logic
├── repository/       # Database access layer
├── model/            # JPA entities
└── config/           # Security & JWT config
```

---

## 🗺️ Roadmap

- [x] Project setup & dependencies
- [x] Database schema & JPA models
- [x] Repository layer
- [ ] User registration & login
- [ ] JWT authentication
- [ ] Account management APIs
- [ ] Deposit & withdrawal
- [ ] Fund transfer with concurrency handling
- [ ] Transaction history
- [ ] Global exception handling
- [ ] Unit tests with JUnit & Mockito
- [ ] Docker setup
- [ ] Swagger documentation

---

## 👨‍💻 Author

**Dipanwita Paul**
[GitHub](https://github.com/dipanwitapaul578)