# Blogger - Full Stack Blogging Platform

A modern, feature-rich blogging platform built with **Spring Boot** and **Angular**. This application provides a complete social blogging experience with user authentication, role-based access control, post management, notifications, and admin dashboard.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Admin Panel](#admin-panel)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Security Features](#security-features)
- [Contributing](#contributing)

## ✨ Features

### User Features
- **Authentication & Authorization**
  - User registration and login with JWT tokens
  - Role-based access control (USER, ADMIN)
  - Secure password hashing with BCrypt
  - Profile management with avatar upload

- **Post Management**
  - Create, edit, and delete posts
  - Rich media support (images, videos, GIFs)
  - Like and comment on posts
  - View post details with engagement metrics

- **Social Features**
  - Follow/unfollow other users
  - Real-time notifications for new posts, likes, and comments
  - View posts from followed users
  - User profiles with bio and statistics

- **Reporting System**
  - Report inappropriate posts
  - Track report status
  - Admin moderation

### Admin Features
- **Dashboard**
  - System statistics overview
  - User management (ban/unban/delete)
  - Post moderation
  - Report management

- **User Management**
  - View all users
  - Ban/unban users
  - Delete user accounts
  - Change user roles

- **Content Moderation**
  - View all posts
  - Delete inappropriate content
  - Review and resolve reports

## 🛠 Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Security**: Spring Security + JWT
- **Database**: MySQL 8.x / PostgreSQL
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Password Encryption**: BCrypt
- **File Storage**: Local file system

### Frontend
- **Framework**: Angular 21.x
- **Language**: TypeScript
- **UI Library**: Angular Material
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router
- **State Management**: Services + RxJS

## 🏗 Architecture

```
blogger/
├── backend/           # Spring Boot REST API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/blog/blogger/
│   │   │   │   ├── config/          # Security & app configuration
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── dto/             # Data transfer objects
│   │   │   │   ├── models/          # JPA entities
│   │   │   │   ├── repository/      # Data access layer
│   │   │   │   ├── security/        # JWT & authentication
│   │   │   │   ├── service/         # Business logic
│   │   │   │   └── exception/       # Error handling
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                    # Unit & integration tests
│   └── pom.xml
│
└── frontend/          # Angular SPA
    ├── src/
    │   ├── app/
    │   │   ├── admin/               # Admin components
    │   │   ├── auth/                # User components
    │   │   ├── core/
    │   │   │   ├── guards/          # Route guards
    │   │   │   └── services/        # API services
    │   │   └── shared/              # Shared components
    │   ├── assets/
    │   └── styles.css
    ├── angular.json
    └── package.json
```

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
- **Node.js 18.x** or higher
- **npm 9.x** or higher
- **MySQL 8.x** or **PostgreSQL 13.x**
- **Maven 3.8+** (or use included Maven wrapper)
- **Angular CLI 21.x**: `npm install -g @angular/cli`

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd blogger
```

### 2. Database Setup

#### Option A: MySQL
```sql
-- Create database
CREATE DATABASE blogger_db;

-- Create user (optional)
CREATE USER 'blogger_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON blogger_db.* TO 'blogger_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Option B: PostgreSQL
```sql
-- Create database
CREATE DATABASE blogger_db;

-- Create user (optional)
CREATE USER blogger_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE blogger_db TO blogger_user;
```

### 3. Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/blogger_db
spring.datasource.username=blogger_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# OR Database Configuration (PostgreSQL)
# spring.datasource.url=jdbc:postgresql://localhost:5432/blogger_db
# spring.datasource.username=blogger_user
# spring.datasource.password=your_password
# spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=your-secret-key-min-256-bits-please-change-this-in-production
jwt.expiration=86400000

# File Upload Configuration
file.upload-dir=uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 4. Frontend Configuration

Edit `frontend/src/app/core/services/*.service.ts` if needed:

```typescript
// Default backend URL is http://localhost:8080
private apiUrl = 'http://localhost:8080/auth';
```

### 5. Install Dependencies

#### Backend
```bash
cd backend
./mvnw clean install
```

#### Frontend
```bash
cd frontend
npm install
```

## 🎯 Running the Application

### Development Mode

#### 1. Start Backend (Terminal 1)
```bash
cd backend
./mvnw spring-boot:run
```
Backend will run on: `http://localhost:8080`

#### 2. Start Frontend (Terminal 2)
```bash
cd frontend
ng serve
```
Frontend will run on: `http://localhost:4200`

### Production Build

#### Backend
```bash
cd backend
./mvnw clean package
java -jar target/blogger-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend
ng build --configuration production
# Serve the dist/frontend folder with a web server
```

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | User login | No |

### Post Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/auth/posts` | Get all posts (paginated) | No |
| GET | `/auth/posts/{id}` | Get post by ID | No |
| POST | `/auth/posts` | Create new post | Yes (USER) |
| PUT | `/auth/posts/{id}` | Update post | Yes (OWNER/ADMIN) |
| DELETE | `/auth/posts/{id}` | Delete post | Yes (OWNER/ADMIN) |
| POST | `/auth/posts/upload` | Upload media file | Yes (USER) |
| POST | `/auth/posts/{id}/like` | Like a post | Yes (USER) |
| DELETE | `/auth/posts/{id}/like` | Unlike a post | Yes (USER) |
| POST | `/auth/posts/{id}/comments` | Add comment | Yes (USER) |

### Admin Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/auth/admin/stats` | Get dashboard stats | Yes (ADMIN) |
| GET | `/auth/admin/users` | Get all users | Yes (ADMIN) |
| PUT | `/auth/admin/users/{id}/ban` | Ban user | Yes (ADMIN) |
| PUT | `/auth/admin/users/{id}/unban` | Unban user | Yes (ADMIN) |
| DELETE | `/auth/admin/users/{id}` | Delete user | Yes (ADMIN) |

### Report Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/reports` | Create report | Yes (USER) |
| GET | `/auth/reports/my` | Get my reports | Yes (USER) |
| GET | `/auth/reports/admin/all` | Get all reports | Yes (ADMIN) |
| PUT | `/auth/reports/admin/{id}` | Update report | Yes (ADMIN) |

### Notification Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/auth/notifications` | Get user notifications | Yes (USER) |
| PUT | `/auth/notifications/{id}/read` | Mark as read | Yes (USER) |

## 👨‍💼 Admin Panel

### Creating an Admin Account

#### Option 1: Using SQL
```sql
-- First, create a regular user through the application
-- Then update their role in the database:
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

#### Option 2: Using the provided script
```bash
cd backend
./create_admin.sh
```

### Accessing Admin Panel

1. Login with admin credentials
2. Navigate to `/admin` in the frontend
3. Admin panel will show:
   - Dashboard with statistics
   - User management
   - Report management
   - Content moderation tools

## 🧪 Testing

### Backend Unit Tests

```bash
cd backend
./mvnw test
```

### Backend Integration Tests

```bash
cd backend
./mvnw verify
```

### Frontend Unit Tests

```bash
cd frontend
ng test
```

### Frontend E2E Tests

```bash
cd frontend
ng e2e
```

## 📁 Project Structure

### Key Backend Files

- `SecurityConfig.java` - Security & JWT configuration
- `JwtUtil.java` - JWT token generation & validation
- `PostController.java` - Post management endpoints
- `AdminController.java` - Admin operations
- `NotificationService.java` - Notification logic
- `FileStorageService.java` - File upload handling

### Key Frontend Files

- `app.routes.ts` - Application routing
- `auth.guard.ts` - Authentication guard
- `admin.guard.ts` - Admin authorization guard
- `post.service.ts` - Post API service
- `auth.service.ts` - Authentication service

## 🔒 Security Features

1. **Password Security**
   - BCrypt hashing (cost factor: 10)
   - No plaintext password storage

2. **Authentication**
   - JWT tokens with expiration
   - Stateless session management
   - Secure token validation

3. **Authorization**
   - Role-based access control (RBAC)
   - Endpoint-level security
   - Frontend route guards

4. **Input Validation**
   - Jakarta Bean Validation
   - File upload validation
   - XSS protection with DomSanitizer

5. **SQL Injection Prevention**
   - JPA/Hibernate parameterized queries
   - No raw SQL queries

6. **File Upload Security**
   - Path traversal prevention
   - File type validation
   - Size limits (10MB)
   - Unique filename generation

## 🐛 Common Issues & Troubleshooting

### Backend Issues

**Issue**: Database connection failed
```bash
# Check MySQL/PostgreSQL is running
sudo systemctl status mysql
# or
sudo systemctl status postgresql
```

**Issue**: Port 8080 already in use
```bash
# Change port in application.properties
server.port=8081
```

### Frontend Issues

**Issue**: CORS errors
- Ensure backend CORS is configured for `http://localhost:4200`
- Check `SecurityConfig.java` corsConfigurationSource()

**Issue**: Build errors
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

## 📝 Environment Variables

For production, use environment variables instead of hardcoded values:

```bash
# Backend
export JWT_SECRET=your-production-secret-key
export DB_PASSWORD=your-db-password
export UPLOAD_DIR=/var/www/uploads

# Frontend
export API_URL=https://api.yourdomain.com
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Spring Boot Documentation
- Angular Documentation
- Angular Material
- JWT.io

## 📞 Support

For support, email support@yourdomain.com or open an issue in the repository.

---

**Note**: Remember to change default passwords and JWT secrets in production!
