# 🏥 Patient Management System

A production-ready **Microservices** project built with **Java Spring Boot** that manages patients, billing, and authentication through independently deployable services — all connected via a centralized API Gateway.

---

## 🔗 Repository

[https://github.com/Mayur2502/Patient-Management](https://github.com/Mayur2502/Patient-Management)

---

## 🏗️ Architecture Overview

```
                        ┌──────────────────────┐
         All Clients ──►│   API Gateway :8080   │
                        └──────────┬───────────┘
                                   │ JWT Validation
              ┌────────────────────┼──────────────────────┐
              ▼                    ▼                       ▼
     ┌────────────────┐  ┌─────────────────┐  ┌────────────────────┐
     │  Auth Service  │  │ Patient Service │  │  Billing Service   │
     │   :9000        │  │    :4000        │  │     :8082          │
     │   MySQL        │  │    H2 / MySQL   │  │     MySQL          │
     └────────────────┘  └─────────────────┘  └────────────────────┘
```

Each service is independently deployable, has its own database, and communicates over REST.

---

## 🧩 Services

### 1. 🔐 Auth Service — `port 9000`
Handles user registration, login, and JWT token generation and validation.

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/auth/register` | No | Register a new user |
| POST | `/auth/login` | No | Login and receive JWT token |
| GET | `/auth/validate` | Yes | Validate a JWT token |

### 2. 👤 Patient Service — `port 4000`
Full CRUD management for patients. Automatically creates a billing account when a new patient is registered.

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| GET | `/patients` | Yes | Get all patients |
| POST | `/patients` | Yes | Create a new patient |
| PUT | `/patients/{id}` | Yes | Update a patient |
| DELETE | `/patients/{id}` | Yes | Delete a patient |

### 3. 💰 Billing Service — `port 8082`
Manages billing accounts and invoices per patient. Billing accounts are auto-created when a patient is registered.

**Billing Accounts**

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/billing-accounts` | Yes | Create a billing account |
| GET | `/api/billing-accounts/{id}` | Yes | Get account by ID |
| GET | `/api/billing-accounts/patient/{patientId}` | Yes | Get account by patient ID |
| GET | `/api/billing-accounts` | Yes | Get all billing accounts |
| PUT | `/api/billing-accounts/{id}/suspend` | Yes | Suspend an account |
| PUT | `/api/billing-accounts/{id}/reactivate` | Yes | Reactivate an account |

**Invoices**

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/invoices` | Yes | Create an invoice |
| GET | `/api/invoices/{id}` | Yes | Get invoice by ID |
| GET | `/api/invoices/account/{accountId}` | Yes | Get invoices by account |
| PUT | `/api/invoices/{id}/pay` | Yes | Mark invoice as paid |
| PUT | `/api/invoices/{id}/cancel` | Yes | Cancel an invoice |

### 4. 🚪 API Gateway — `port 8080`
Single entry point for all services. Validates JWT tokens and routes requests to the correct service.

| Route | Forwards To | Auth Required |
|-------|-------------|---------------|
| `/auth/**` | Auth Service :9000 | No |
| `/patients/**` | Patient Service :4000 | Yes |
| `/api/**` | Billing Service :8082 | Yes |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Primary language |
| Spring Boot 3.x | Microservice framework |
| Spring Security | Authentication & authorization |
| Spring Cloud Gateway | API Gateway and routing |
| Spring Data JPA | Database ORM |
| JWT (jjwt 0.12.6) | Token generation and validation |
| MySQL | Production database |
| H2 | In-memory database (Patient Service dev) |
| Hibernate | ORM implementation |
| Maven | Dependency management |

---

## 📁 Project Structure

```
Patient-Management/
│
├── api-gateway/                  # Spring Cloud Gateway
│   └── src/main/java/com/pm/apigateway/
│       ├── filter/
│       │   └── JwtAuthFilter.java
│       └── util/
│           └── JwtUtil.java
│
├── auth-service/                 # Authentication & JWT
│   └── src/main/java/com/pm/authservice/
│       ├── controller/
│       ├── service/
│       ├── security/
│       ├── model/
│       ├── repository/
│       ├── dto/
│       └── exception/
│
├── patient-service/              # Patient CRUD
│   └── src/main/java/com/pm/patientservice/
│       ├── controller/
│       ├── service/
│       ├── model/
│       ├── repository/
│       ├── dto/
│       ├── mapper/
│       ├── external/
│       └── exception/
│
└── billing-service/              # Billing & Invoices
    └── src/main/java/com/pm/billingservice/
        ├── controller/
        ├── service/
        ├── model/
        ├── repository/
        ├── dto/
        └── exception/
```

---

## ⚙️ Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 21+
- Maven 3.8+
- MySQL 8+
- Postman (for testing)
- IntelliJ IDEA (recommended)

### Database Setup

Create the following databases in MySQL before running the services:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE billing_db;
```

> The Patient Service uses H2 in-memory database by default — no setup needed.

### Running the Services

Start each service in this exact order:

```bash
# 1. Auth Service
cd auth-service
mvn spring-boot:run

# 2. Patient Service
cd patient-service
mvn spring-boot:run

# 3. Billing Service
cd billing-service
mvn spring-boot:run

# 4. API Gateway (start last)
cd api-gateway
mvn spring-boot:run
```

### Service Ports

| Service | Port |
|---------|------|
| API Gateway | 8080 |
| Auth Service | 9000 |
| Patient Service | 4000 |
| Billing Service | 8082 |

---

## 🔑 Authentication Flow

All requests (except `/auth/login` and `/auth/register`) require a valid JWT token.

**Step 1 — Register a user:**
```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "email": "admin@hospital.com",
  "password": "admin123",
  "role": "ROLE_ADMIN"
}
```

**Step 2 — Login to get token:**
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "admin@hospital.com",
  "password": "admin123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ROLE_ADMIN",
  "email": "admin@hospital.com"
}
```

**Step 3 — Use token in all requests:**
```http
GET http://localhost:8080/patients
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 🔄 Key Flows

### Patient Registration Flow
```
POST /patients
      │
      ▼
Patient Service saves patient
      │
      ▼
Calls Billing Service → POST /api/billing-accounts
      │
      ▼
Billing account auto-created and linked to patient
```

### Invoice Flow
```
POST /api/invoices  (status: PENDING)
      │
      ├── PUT /api/invoices/{id}/pay     → status: PAID
      └── PUT /api/invoices/{id}/cancel  → status: CANCELLED
```

---

## 🛡️ Exception Handling

Every service has a global exception handler that returns consistent error responses:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Patient not found with ID: abc-123",
  "timestamp": "2026-05-17T10:30:00"
}
```

| Exception | HTTP Status |
|-----------|-------------|
| Resource not found | 404 Not Found |
| Duplicate resource | 409 Conflict |
| Invalid operation | 400 Bad Request |
| Unauthorized / bad token | 401 Unauthorized |
| Billing service down | 503 Service Unavailable |
| Unexpected error | 500 Internal Server Error |

---

## 📝 Environment Variables

Each service uses `application.properties`. Key values to configure:

**Auth Service & API Gateway:**
```properties
jwt.secret=your-base64-encoded-secret-key
jwt.expiration=86400000
```

**All services with MySQL:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=root
spring.datasource.password=yourpassword
```

**Patient Service:**
```properties
billing.service.url=http://localhost:8082
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## 👨‍💻 Author

**Mayur** — [github.com/Mayur2502](https://github.com/Mayur2502)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
