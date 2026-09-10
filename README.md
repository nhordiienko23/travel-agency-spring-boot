# ✈️ Travel Agency Management System

A full-stack **Spring Boot Travel Agency Management System** for managing tours, bookings, users, balances, and role-based access. The project combines a server-rendered Thymeleaf web application with a REST API and implements stateless JWT authentication, validation, internationalization, centralized exception handling, AOP-based audit logging, pagination, filtering, sorting, and Swagger/OpenAPI documentation.

## ✨ Features

### Authentication & Security

- Stateless authentication with **JWT** and **Spring Security 6**.
- JWT can be supplied through the `Authorization: Bearer <token>` header or through the `JWT` HttpOnly cookie used by the web interface.
- Passwords are stored as **BCrypt hashes** rather than plain text.
- Role-based authorization with `ADMIN`, `MANAGER`, and `USER` roles.
- Custom handling for `401 Unauthorized` and `403 Forbidden` responses.
- HTTP sessions are disabled with `SessionCreationPolicy.STATELESS`.

### Tour Management

- Browse available tours.
- Filter tours by keyword, tour type, transfer type, hotel type, price, hot status, status, and date range.
- Pagination and sorting.
- **Hot tours are prioritized** in tour listings.
- Admin can create, edit, and delete tours.
- Manager can change a tour's hot status and update its status.
- Supported tour statuses: `REGISTERED`, `PAID`, `CANCELED`.

### Booking & Balance

- Registered users can order available tours.
- Tour price is deducted from the user's internal balance when a booking is made.
- Users can deposit money into their account.
- Users can view their booked tours and booking history.
- Users can cancel their own bookings according to the business rules.
- Refund processing is supported when a paid tour is cancelled.

### User Management

- User registration and login.
- Profile information can be updated.
- Password changes are supported.
- Users can delete their own accounts.
- Administrators can search users, change account status, and deposit money to a user's balance.
- User search supports pagination and username sorting.

### Internationalization

The application supports:

- English (`EN`)
- Russian (`RU`)

Messages are resolved through Spring's `MessageSource` and the locale can be changed with the `lang` request parameter.

### Logging & Auditing

Business operations are logged using **Spring AOP** and the custom `@Loggable` annotation.

The project records:

- operation start (`DEBUG`);
- successful business operations (`INFO`);
- expected business/security failures (`WARN`);
- unexpected application errors (`ERROR`).

Application logs are written both to the normal SLF4J application log and asynchronously to PostgreSQL in the `application_logs` table. `AuditContext` is used to pass additional business details from service methods to the logging aspect, while sensitive values such as passwords and tokens are masked.

### Error Handling

A centralized REST exception handler provides consistent JSON responses for:

- validation errors → `400 Bad Request`;
- resource not found → `404 Not Found`;
- invalid business arguments → `400 Bad Request`;
- invalid credentials → `401 Unauthorized`;
- blocked accounts / denied access → `403 Forbidden`;
- unexpected errors → `500 Internal Server Error`.

Internal exception details are logged but are not exposed to API clients.

### REST API

The application exposes REST endpoints under `/api`.

#### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

#### Users

```text
GET    /api/users/search
GET    /api/users/{id}
GET    /api/users/username/{username}
PATCH  /api/users/status
PATCH  /api/users/profile
PATCH  /api/users/password
POST   /api/users/balance/deposit
POST   /api/users/balance/deposit/admin
DELETE /api/users/me
```

#### Tours

```text
GET    /api/vouchers
GET    /api/vouchers/user/{userId}
POST   /api/vouchers
PATCH  /api/vouchers/{id}
DELETE /api/vouchers/{id}
PATCH  /api/vouchers/{id}/hot
```

Administrative operations are protected with method-level `@PreAuthorize` checks.

### Swagger / OpenAPI

Interactive API documentation is available through Springdoc OpenAPI:

```text
http://localhost:8080/swagger-ui/index.html
```

The Swagger configuration defines JWT Bearer authentication for protected API endpoints.

## 👥 Roles & Permissions

| Role | Capabilities |
|---|---|
| **ADMIN** | Full system access: manage tours, manage users, change account status, deposit to user balances, and perform manager operations |
| **MANAGER** | Manage tour status and hot status; limited user management permissions |
| **USER** | Browse tours, order tours, manage profile, deposit balance, view personal tours, and cancel own bookings |

## 🏗️ Architecture

The project is organized by business/domain areas rather than one large shared controller/service package:

```text
com.epam.finaltask
├── aspect      # AOP business-operation logging
├── auth        # registration, login, UserDetails integration
├── core        # home page, common DTOs, configuration and exceptions
├── log         # application log entity, service and audit context
├── security    # JWT filter, JWT utilities and security handlers
├── user        # users, roles, controllers, services, repositories and DTOs
└── voucher     # tours/vouchers, DTOs, controllers, services and repositories
```

The application follows a layered approach:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

DTOs are used at the controller/service boundary to avoid exposing JPA entities directly through the REST API.

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.2.1**
- Spring MVC
- Spring Data JPA / Hibernate
- Spring Security 6
- JWT with JJWT 0.11.5
- BCrypt password hashing
- PostgreSQL 16
- H2 for test/runtime test configuration
- Thymeleaf
- Bootstrap 4
- Springdoc OpenAPI / Swagger UI
- Lombok
- JUnit 5
- Mockito
- Spring Security Test
- JaCoCo
- Maven
- Docker Compose

## 🗄️ Database

The main entities are:

- `users` — application users, roles, account status, balance and profile data.
- `vouchers` — tours, prices, travel options, dates, status and current booking owner.
- `application_logs` — persisted application and audit logs.

PostgreSQL is provided through Docker Compose.

## 🚀 Run Locally

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker Desktop / Docker Engine
- Git

### 1. Clone the repository

```bash
git clone https://github.com/nhordiienko23/travel-agency-spring-boot.git
cd travel-agency-spring-boot
```

### 2. Start PostgreSQL

The provided `docker-compose.yml` starts PostgreSQL 16 on port `5432`.

```bash
docker compose up -d
```

The default database configuration is:

```text
Database: travel_agency
Username: postgres
Password: password
Host: localhost
Port: 5432
```

The username and password can be overridden with the `Database_Username` and `Database_Password` environment variables.

### 3. Build and run the application

```bash
mvn clean install
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

The default Spring profile is `prod`, which uses PostgreSQL.

### 4. Open the application

Web application:

```text
http://localhost:8080/
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

The repository contains `data.sql` with demo users and tour data that are loaded on startup.

> **Security note:** the repository currently contains development/default JWT and database configuration values. For a real deployment, replace them with environment variables or a secret-management solution and use a unique JWT secret.

## 🧪 Testing

The project contains unit and controller-level tests covering authentication, security, services, controllers, DTO validation, mapping, specifications, logging, and exception handling.

The checked-in build report contains:

- **693 tests**
- **0 failures**
- **0 errors**
- **100% instruction coverage**
- **100% branch coverage**
- **100% line coverage**
- **100% method coverage**
- **100% class coverage**

Run tests with:

```bash
mvn test
```

Generate the JaCoCo report with:

```bash
mvn clean test
```

Then open:

```text
target/site/jacoco/index.html
```

## 📸 Screenshots

### Dashboard

<img width="1920" height="974" alt="image" src="https://github.com/user-attachments/assets/d124cf3a-c2e3-4aa4-b18e-aec8dfafb93c" />

### Manager Panel

<img width="1920" height="978" alt="image" src="https://github.com/user-attachments/assets/827eaf60-2791-4789-9842-0a2e24574527" />


### Admin Panel for managing users

<img width="1920" height="950" alt="image" src="https://github.com/user-attachments/assets/9e779523-10eb-4a79-ae75-3f1a38037b50" />

### Admin Panel for managing vouchers
<img width="1920" height="985" alt="image" src="https://github.com/user-attachments/assets/cbe5d085-76a9-4ae2-b8a2-142cd9ce41f2" />

### Springdoc OpenAPI / Swagger UI
<img width="1920" height="960" alt="image" src="https://github.com/user-attachments/assets/fad3ac26-5b38-4f53-b55f-be8a2b331598" />


## 📌 Project Highlights

This project demonstrates practical use of:

- Spring Boot application design;
- REST API development;
- server-side rendering with Thymeleaf;
- Spring Data JPA repositories and specifications;
- stateless JWT authentication;
- role-based access control;
- DTO-based API design;
- Bean Validation;
- centralized exception handling;
- English/Russian i18n;
- AOP-based business logging and audit trails;
- pagination, filtering and sorting;
- PostgreSQL + Docker;
- automated testing and code coverage.
- Springdoc OpenAPI / Swagger UI

## 📄 License

This project was created as a personal / educational Spring Boot project.
