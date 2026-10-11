# Vetvision Backend

Backend service for **Vetvision** — Animal Healthcare and Veterinary Assistance Management System.

Built with **Spring Boot 3.4.4** and **Java 21**.

## Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control (Admin, Veterinarian, Clinic, Pet Owner).
- **Clinic Management**: Register, list, and update veterinary clinics.
- **Appointment Scheduling**: Book, manage, and update statuses of veterinary appointments.
- **Animal Case Management**: Track animal cases, status history, and medical records.
- **Treatment Records**: Record diagnoses, prescribed treatments, medications, and clinical notes.
- **Dashboard Analytics**: Statistical overview and reporting endpoints for clinic management.

## Tech Stack

- **Java 21**
- **Spring Boot 3.4.4**
- **Spring Data JPA & Hibernate**
- **Spring Security & JJWT (0.12.5)**
- **MySQL 8** (Aiven Cloud or local)
- **Maven**

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+ (or Maven wrapper)
- MySQL database

### Configuration

Database and application configuration can be adjusted in `src/main/resources/application.properties` or overridden via environment variables:

- `PORT` or `SERVER_PORT`: Application port (default: `8083`)
- `DB_URL`: JDBC database URL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT signing
- `FRONTEND_ORIGIN`: Allowed CORS frontend origin (default: `http://127.0.0.1:5173`)

### Running Locally

```bash
mvn clean spring-boot:run
```

The service will start by default on `http://localhost:8083`.
