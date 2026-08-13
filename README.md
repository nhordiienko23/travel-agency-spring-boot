# ✈️ Travel Agency Management System

A comprehensive, stateless web application for managing travel tours, bookings, and user accounts. Built with a focus on clean architecture, secure role-based access control, and a responsive user interface.

## 🚀 Key Features

* **Role-Based Access Control (RBAC):** Three distinct access levels (`ADMIN`, `MANAGER`, `USER`) with secure routing and endpoint protection.
* **Stateless Authentication:** Fully implemented JWT (JSON Web Token) authentication mechanism (cookie & header based) with disabled HTTP sessions.
* **Tour Management:** Advanced sorting, dynamic filtering, and pagination. Business rules enforced (e.g., "Hot 🔥" tours always displayed at the top).
* **Responsive UI:** Custom dark-themed interface built with Thymeleaf, Bootstrap 4, and SB Admin 2.
* **Internationalization (i18n):** Seamless switching between English (EN) and Russian (RU) locales.
* **API Documentation:** Auto-generated interactive API documentation via Springdoc OpenAPI (Swagger UI).
* **High Test Coverage:** Comprehensive Unit Tests utilizing JUnit 5, Mockito, and MockMvc standalone setups (74/74 passing).

## 🛠️ Tech Stack

* **Core:** Java 17/21, Spring Boot 3
* **Security:** Spring Security 6, JWT, BCrypt
* **Data Access:** Spring Data JPA, Hibernate, H2 (Testing) / PostgreSQL (Production)
* **Frontend:** Thymeleaf, Bootstrap, HTML5/CSS3
* **Tools:** Maven, MapStruct, Lombok, Git

## 👥 User Roles & Permissions

1. **Administrator (`ADMIN`):**
   * Has full access to manage the system.
   * Can create, update, and delete tours.
   * Manages user accounts (block/unblock features).
2. **Manager (`MANAGER`):**
   * Manages tour statuses (`REGISTERED`, `PAID`, `CANCELED`).
   * Can toggle the `HOT` status for specific tours.
3. **Client (`USER`):**
   * Can browse available tours with advanced filters and sorting.
   * Can purchase tours (deducts from the internal account balance).
   * Can manage their personal profile and view purchase history.

## 💻 Running the Application Locally

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/nhordiienko23/travel-agency-spring-boot.git](https://github.com/nhordiienko23/travel-agency-spring-boot.git)
   cd travel-agency-spring-boot

**Build and run the application:**
```bash
mvn clean install
mvn spring-boot:run
```
**Access the application:**
UI Dashboard:
```
http://localhost:8080/
```
Swagger API Docs:
```
[http://localhost:8080/](http://localhost:8080/swagger-ui/index.html)
```
(Initial data including default Admin and Manager accounts are automatically populated on startup via data.sql)

# Screenshots

## Dashboard:

<img width="1920" height="1025" alt="image" src="https://github.com/user-attachments/assets/7aa949f1-0d36-4d5f-aa8e-dd5f269e1b78" />

## Manager Panel:

<img width="1920" height="964" alt="image" src="https://github.com/user-attachments/assets/cb8948ad-f704-45b8-ab22-e56a9cf1bea7" />

## Admin Panel:

<img width="1920" height="922" alt="image" src="https://github.com/user-attachments/assets/a1db1bac-5cf2-4d6d-8f1d-ab5ad4aec56b" />
