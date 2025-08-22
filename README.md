# JWT & MFA Authentication API

Spring Boot REST API implementing JWT authentication and TOTP-based Multi-Factor Authentication.

## Features

- User registration and login with BCrypt password hashing
- Stateless JWT authentication 
- Role-based access control (USER/ADMIN roles)
- TOTP multi-factor authentication (Google Authenticator compatible)
- H2 in-memory database
- Simple frontend for testing

## Tech Stack

- Java 17, Spring Boot 3, Spring Security 6
- Spring Data JPA, H2 Database
- Maven, Lombok

## Quick Start

```bash
git clone https://github.com/thonmay/JWT-MFA-Authentication-API
cd jwt-mfa-api
mvn spring-boot:run
```

Application runs on `http://localhost:8080`

**Default admin user:** `admin` / `adminpass`

**H2 Console:** `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa` 
- Password: `password`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login (returns JWT)
- `POST /api/auth/verify-mfa` - Complete MFA login

### Protected Routes
- `GET /api/user/data` - User data (requires USER role)
- `GET /api/admin/data` - Admin data (requires ADMIN role)

### Multi-Factor Authentication
- `POST /api/mfa/setup` - Initialize MFA setup
- `POST /api/mfa/verify` - Verify TOTP code to enable MFA

## Authentication Flow

1. **Standard Login:** POST credentials to `/api/auth/login`
2. **MFA Login:** If MFA enabled, use token from login response with TOTP code at `/api/auth/verify-mfa`
3. **Protected Requests:** Include JWT in `Authorization: Bearer <token>` header

## Project Structure

```
src/main/java/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── model/          # JPA entities
├── dto/            # Data transfer objects
└── security/       # JWT filter and security config
```
