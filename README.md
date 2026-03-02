# Banking System API

A production-grade RESTful Banking System built with Java & Spring Boot.

## Features
- User registration & JWT authentication
- Create & manage bank accounts (Savings/Current)
- Deposit, Withdraw & Transfer funds
- Concurrency-safe transactions using pessimistic locking
- Role-based access control (Admin/Customer)
- Transaction history
- Input validation & global exception handling

## Tech Stack
- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- MySQL + Spring Data JPA
- Lombok
- Maven

## Architecture
```
Controller → Service → Repository → Database
```

## Setup
1. Clone the repo
2. Create MySQL database: `CREATE DATABASE banking_system;`
3. Copy `application.properties.example` to `application.properties`
4. Fill in your DB credentials
5. Run `BankingSystemApplication.java`

## API Endpoints
_Coming soon as features are built_