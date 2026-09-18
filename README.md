# Project KEYSTONE — Field Service Management Platform

A new Java full-stack implementation of a field-service management platform for the Zidio project brief.

## Stack
- Java 21
- Spring Boot 3.4
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- React + TypeScript + Vite
- OpenAPI / Swagger UI
- Docker Compose

## Main capabilities
- Four roles: MANAGER, DISPATCHER, TECHNICIAN, CUSTOMER
- JWT authentication and server-side role checks
- Customer and site management
- Work-order CRUD
- Governed work-order lifecycle with append-only status history
- Technician assignment
- Parts usage with transactional stock decrement
- Technician time logging
- SLA due dates and breach/at-risk reporting
- Manager dashboard
- Customer portal
- Swagger documentation
- Seed data generated with BCrypt password hashing

## Project structure
```text
keystone/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/keystone/
│       ├── controller/
│       ├── domain/
│       ├── dto/
│       ├── exception/
│       ├── repository/
│       ├── security/
│       └── service/
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/
├── frontend/
│   ├── package.json
│   └── src/
├── docker-compose.yml
├── DEMO_SCRIPT.md
├── FEEDBACK_SCRIPT.md
├── PROJECT_REPORT.md
└── DEPLOYMENT.md
```

## Run locally

### 1. Start PostgreSQL
```bash
docker compose up -d postgres
```

### 2. Start the backend
```bash
cd backend
mvn spring-boot:run
```

Backend:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### 3. Start the frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend:
- http://localhost:5173

The frontend uses `VITE_API_URL` and defaults to `http://localhost:8080`.

## Seed accounts

On first backend startup, four accounts are created if they do not already exist. Passwords are generated and stored using BCrypt.

| Role | Email | Password |
|---|---|---|
| Manager | manager@keystone.local | Password123! |
| Dispatcher | dispatcher@keystone.local | Password123! |
| Technician | technician@keystone.local | Password123! |
| Customer | customer@keystone.local | Password123! |

For a real deployment, change these credentials and the JWT secret.

## Important API endpoints
- `POST /api/auth/login`
- `GET /api/customers`
- `POST /api/customers`
- `GET /api/customers/{id}/sites`
- `POST /api/customers/{id}/sites`
- `GET /api/work-orders`
- `POST /api/work-orders`
- `GET /api/work-orders/{id}`
- `PUT /api/work-orders/{id}`
- `POST /api/work-orders/{id}/assign`
- `POST /api/work-orders/{id}/status`
- `POST /api/work-orders/{id}/parts`
- `POST /api/work-orders/{id}/time`
- `GET /api/reports/summary`

## Originality
This repository is a fresh implementation written for this submission. The uploaded project brief was used as the requirements source. No trainee repository was copied. AI assistance may be used during development, but the submitted code should be reviewed and understood by the person submitting it.

## Before submission
1. Run the backend and frontend from a clean checkout.
2. Test each role.
3. Deploy backend + frontend + PostgreSQL.
4. Put real deployment URLs into the submission form.
5. Record the 3–5 minute demo using `DEMO_SCRIPT.md`.
6. Record the reflection using `FEEDBACK_SCRIPT.md`.
7. Export `PROJECT_REPORT.md` to PDF if the submission portal requires PDF.
