# Authify

A robust Spring Boot authentication service that provides secure user authentication, registration, and password management capabilities with JWT tokens and email verification.

## 🚀 Features

- **User Authentication** - Secure login with JWT token generation
- **User Registration** - Email-based registration with verification
- **Password Reset** - OTP-based password recovery system
- **Email Integration** - SMTP-based email notifications
- **Security** - Spring Security with JWT authentication
- **Database** - PostgreSQL with JPA/Hibernate
- **API Versioning** - RESTful API with versioning support

## 🛠️ Tech Stack

- **Java 21** - Modern Java with latest features
- **Spring Boot 4.0.3** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction layer
- **PostgreSQL** - Primary database
- **JWT (JJWT)** - JSON Web Token implementation
- **Lombok** - Code generation and boilerplate reduction
- **Spring Mail** - Email service integration

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- PostgreSQL database
- SMTP server for email functionality

## 🚀 Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/your-username/authify.git
cd authify
```

### 2. Configure Database

Create a PostgreSQL database named `authify-db` on port `8092` or modify the configuration in `application.yml`.

### 3. Environment Variables

Create a `.env` file in the project root with the following variables:

```env
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret_key
SMTP_HOST=your_smtp_host
SMTP_PORT=your_smtp_port
SMTP_USERNAME=your_smtp_username
SMTP_PASSWORD=your_smtp_password
```

### 4. Run the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using Maven
mvn spring-boot:run
```

The application will start on `http://localhost:8080` with API context path `/api/v1.0`.

## 📚 API Documentation

### Authentication Endpoints

#### Login
```http
POST /api/v1.0/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "email": "user@example.com",
  "token": "jwt_token_here"
}
```

#### Check Authentication Status
```http
GET /api/v1.0/is-authenticated
```

Response:
```json
true
```

#### Send Reset OTP
```http
POST /api/v1.0/send-reset-otp?email=user@example.com
```

#### Reset Password
```http
POST /api/v1.0/reset-password
Content-Type: application/json

{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "newPassword123"
}
```

### Profile Endpoints

#### Get Profile
```http
GET /api/v1.0/profile
Authorization: Bearer jwt_token
```

#### Update Profile
```http
PUT /api/v1.0/profile
Authorization: Bearer jwt_token
Content-Type: application/json

{
  "name": "Updated Name"
}
```

## 🔧 Configuration

### Application Configuration

The main configuration is in `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: authify
  datasource:
    url: jdbc:postgresql://localhost:8092/authify-db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
  mail:
    host: ${SMTP_HOST}
    port: ${SMTP_PORT}
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}

jwt:
  secret:
    key: ${JWT_SECRET}

server:
  servlet:
    context-path: /api/v1.0
```

## 🏗️ Project Structure

```
src/main/java/com/frostyfox/authify/
├── config/                 # Security and authentication configuration
├── controller/             # REST API controllers
├── dao/                    # Data access objects
├── filter/                 # JWT authentication filter
├── io/                     # Request/Response DTOs
├── model/                  # JPA entities
├── service/                # Business logic services
└── util/                   # Utility classes (JWT, etc.)
```

## 🔐 Security Features

- **JWT Authentication** - Stateless authentication with JWT tokens
- **HTTP-Only Cookies** - Secure token storage in cookies
- **Password Encryption** - BCrypt password hashing
- **CORS Configuration** - Cross-origin resource sharing setup
- **Input Validation** - Request validation using Jakarta Bean Validation

## 📧 Email Features

- **Account Verification** - Email verification for new registrations
- **Password Reset** - OTP-based password recovery
- **SMTP Integration** - Configurable email service

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

## 📦 Build & Deployment

### Build the Application

```bash
./mvnw clean package
```

### Run with Docker (Optional)

Create a `Dockerfile`:

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/authify-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:

```bash
docker build -t authify .
docker run -p 8080:8080 authify
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

