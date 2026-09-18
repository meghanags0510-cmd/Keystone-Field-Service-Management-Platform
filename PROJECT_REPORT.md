# Project KEYSTONE — Project Report

## 1. Overview
KEYSTONE is a field service management platform designed to replace spreadsheet and messaging-based coordination with a single system of record for customer requests, work orders, assignments, field updates, SLA monitoring and reporting.

## 2. Objectives
- Track every request as a work order.
- Give dispatchers assignment and board visibility.
- Give technicians a responsive field workflow.
- Give managers operational and SLA visibility.
- Give customers self-service status tracking.
- Enforce security and lifecycle rules on the server.

## 3. Technology Stack
### Backend
Java 21, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, PostgreSQL, Flyway, Bean Validation and springdoc-openapi.

### Frontend
React, TypeScript and Vite.

### Deployment
Docker Compose for local development; the application is prepared for a cloud backend/frontend/database deployment.

## 4. Architecture
The system follows a layered architecture:

Browser → React SPA → REST Controllers → Services → JPA Repositories → PostgreSQL

Security is applied through Spring Security and a JWT filter. Controllers remain thin; business rules and transactions live in services. DTOs form the API boundary.

## 5. Main Domain
- Customer
- Site
- User
- WorkOrder
- WorkOrderStatusHistory
- Part
- PartUsage
- TimeLog

## 6. Work-Order Lifecycle
NEW → ASSIGNED → IN_PROGRESS → ON_HOLD → IN_PROGRESS → COMPLETED → CLOSED

Cancellation is supported before terminal completion. Illegal transitions are rejected by the service layer, and each valid status change creates an append-only history record.

## 7. Security
The application uses stateless JWT authentication. Passwords are encoded using BCrypt. Protected endpoints use role-based authorization. Customer and technician access is scoped to the data they are allowed to operate on.

## 8. Data Integrity
Part usage and stock decrement are performed inside one transaction. Stock is prevented from going below zero. Status history is append-only.

## 9. API
Representative endpoints:
- POST `/api/auth/login`
- GET/POST `/api/customers`
- GET/POST `/api/customers/{id}/sites`
- GET/POST `/api/work-orders`
- PUT `/api/work-orders/{id}`
- POST `/api/work-orders/{id}/assign`
- POST `/api/work-orders/{id}/status`
- POST `/api/work-orders/{id}/parts`
- POST `/api/work-orders/{id}/time`
- GET `/api/reports/summary`

## 10. Testing Approach
The important tests should cover:
- valid and invalid login
- role restrictions
- customer cross-organisation access
- technician assignment restrictions
- illegal lifecycle transitions
- stock cannot become negative
- status history is created
- validation errors are structured

## 11. Challenges
The key implementation challenge is coordinating authorization, state transitions, audit history and transactions without placing business rules in the React interface.

## 12. Conclusion
KEYSTONE demonstrates a complete Java full-stack architecture with a Spring Boot API, relational persistence, JWT security, React UI and a governed operational workflow. It is structured for clean local setup and cloud deployment.
