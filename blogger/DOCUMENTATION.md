# Blogger App Documentation

## Overview
This project is a full-stack blog platform with:
- Backend: Spring Boot REST API with JWT authentication and MySQL.
- Frontend: Angular SPA with guards, interceptors, and feature-based modules.
- Media: file uploads stored locally and served from `/uploads`.

## Backend (Spring Boot)

### Tech Stack / Dependencies
- Spring Boot Web
- Spring Data JPA
- Spring Security
- Spring Validation
- JWT (jjwt)
- MySQL Connector/J
- Lombok

### Configuration
Main settings: `blogger/backend/src/main/resources/application.properties`
- Server: `server.port=8080`
- DB: `spring.datasource.url=jdbc:mysql://localhost:3307/blog_db`
- JWT: `jwt.secret`, `jwt.expiration`
- Uploads:
  - `file.upload-dir=uploads`
  - `spring.servlet.multipart.max-file-size=50MB`
  - `spring.servlet.multipart.max-request-size=50MB`

### Security
- Stateless JWT auth with custom filter.
- Public endpoints include register, login, and some read-only endpoints.
- Uploads are served publicly from `/uploads/**`.

### Main Packages
- `com.blog.blogger.controller`: REST controllers
- `com.blog.blogger.service`: business logic
- `com.blog.blogger.repository`: JPA repositories
- `com.blog.blogger.models`: entities
- `com.blog.blogger.security`: JWT + security config
- `com.blog.blogger.dto`: request/response DTOs
- `com.blog.blogger.config`: configuration (security, admin init)

### REST Endpoints
Base URL: `http://localhost:8080`

Auth (`/auth`)
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/home` (test endpoint)

Users (`/auth/users`)
- `GET /auth/users/me`
- `GET /auth/users/{id}`
- `PUT /auth/users/{id}`
- `PUT /auth/users/{id}/password`
- `DELETE /auth/users/{id}`
- `GET /auth/users`
- `POST /auth/users/{userId}/follow`
- `DELETE /auth/users/{userId}/follow`
- `GET /auth/users/{userId}/is-following`
- `GET /auth/users/following`
- `GET /auth/users/followers`
- `POST /auth/users/upload-profile-picture` (multipart)

Posts (`/auth/posts`)
- `GET /auth/posts` (paged)
- `GET /auth/posts/following`
- `GET /auth/posts/{id}`
- `POST /auth/posts`
- `POST /auth/posts/upload` (multipart image/video, max 50MB)
- `PUT /auth/posts/{id}` (owner only)
- `DELETE /auth/posts/{id}` (owner or admin)
- `POST /auth/posts/{postId}/comments`
- `POST /auth/posts/{id}/like`
- `DELETE /auth/posts/{id}/like`
- `GET /auth/posts/{id}/liked`
- `POST /auth/posts/{postId}/comments/{commentId}/like`
- `DELETE /auth/posts/{postId}/comments/{commentId}/like`
- `GET /auth/posts/{postId}/comments/{commentId}/liked`
- `DELETE /auth/posts/{postId}/comments/{commentId}`

Reports (`/auth/reports`)
- `POST /auth/reports`
- `GET /auth/reports/my`
- `GET /auth/reports/admin/all`
- `GET /auth/reports/admin/unresolved`
- `GET /auth/reports/admin/count-unresolved`
- `PUT /auth/reports/admin/{id}`
- `DELETE /auth/reports/admin/{id}`

Admin (`/auth/admin`) - requires ADMIN role
- `GET /auth/admin/stats`
- `GET /auth/admin/users`
- `PUT /auth/admin/users/{id}/ban`
- `PUT /auth/admin/users/{id}/unban`
- `PUT /auth/admin/users/{id}/role`
- `DELETE /auth/admin/users/{id}`
- `GET /auth/admin/posts` (paged)
- `PUT /auth/admin/posts/{id}/hide`
- `PUT /auth/admin/posts/{id}/unhide`
- `DELETE /auth/admin/posts/{id}`

Notifications (`/auth/notifications`)
- `GET /auth/notifications`
- `GET /auth/notifications/paginated`
- `GET /auth/notifications/unread`
- `GET /auth/notifications/unread/count`
- `PUT /auth/notifications/{id}/read`
- `PUT /auth/notifications/read-all`
- `DELETE /auth/notifications/{id}`
- `DELETE /auth/notifications/read`

Uploads
- `GET /uploads/{filename}` (serve media)

### Running the Backend
From `blogger/backend`:
```
./mvnw spring-boot:run
```

## Frontend (Angular)

### Tech Stack / Dependencies
Angular (20.x), Angular Material, RxJS, TypeScript.

### Structure
Main folders under `blogger/frontend/src/app`:
- `core/`:
  - JWT interceptor
  - Auth/admin/guest guards
  - API services (auth, users, posts, reports, notifications, messages)
- `auth/`: login, register, home feed, profile, settings, chat, notifications
- `admin/`: dashboard, reports, user management, posts moderation
- `shared/`: not-found, unauthorized, shared components

### Frontend Features
- Auth with JWT stored client-side and attached by interceptor
- Post feed with media display
- Profile editing and profile picture upload
- Admin moderation (posts, reports, users)

### Running the Frontend
From `blogger/frontend`:
```
npm install
npm start
```

## Docker (optional)
There are Dockerfiles for backend/frontend and a `docker-compose.yml` in `blogger/` if you prefer containerized runs.
