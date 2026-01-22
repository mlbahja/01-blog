



# Principles Used in This App

This file summarizes the main Spring Boot and Angular principles used in the project.

## Spring Boot Principles

- Layered architecture: controllers handle HTTP, services contain business logic, repositories handle data access.
- Dependency injection: Spring creates and wires beans for controllers, services, and repositories.
- RESTful API design: endpoints follow resource-based URLs and HTTP methods (GET, POST, PUT, DELETE).
- DTO usage: request/response objects isolate external API contracts from entity models.
- Validation: request validation is handled via `spring-boot-starter-validation` and `@Valid`.
- JPA entities: domain models are mapped to tables using JPA annotations.
- Repository pattern: `JpaRepository` abstracts CRUD and query operations.
- Transaction management: service methods use `@Transactional` for multi-step DB updates.
- Security: stateless JWT auth with a filter that validates tokens per request.
- CORS: cross-origin rules allow the Angular frontend to call the API.
- File uploads: multipart handling with server-side size limits and storage on disk.

## Angular Principles

- Component-based UI: each screen is built from reusable components.
- Modules and routing: features are grouped and routed by Angular modules and routes.
- Services for API calls: HTTP logic lives in injectable services.
- Dependency injection: components/services receive dependencies via constructors.
- Interceptors: JWT token is attached to API requests through a request interceptor.
- Guards: route access is restricted based on auth and role checks.
- Reactive patterns: async API calls use RxJS observables.
- Templates + binding: HTML templates bind to component state and events.
- Separation of concerns: UI logic stays in components; network logic stays in services.

--------------------------------------


# Principles Used in This App (Detailed)

This README explains the main Spring Boot and Angular principles used in your project, with more detail on how and why they are applied.

## Spring Boot Principles

### 1) Layered Architecture
The backend is organized by responsibility: controllers handle HTTP requests and responses, services hold the business logic, and repositories handle database access. This keeps each class focused on a single role and makes the code easier to test and maintain.

### 2) Dependency Injection
Spring creates and wires your controllers, services, and repositories automatically. Instead of creating objects with `new`, Spring injects them through constructors or fields, which makes the code more flexible and easier to test.

### 3) RESTful API Design
Endpoints are designed around resources and HTTP verbs. For example, `GET` reads data, `POST` creates, `PUT` updates, and `DELETE` removes. This creates predictable APIs and makes it easier to integrate with the frontend.

### 4) DTO (Data Transfer Object) Usage
DTOs are used for input and output to avoid exposing internal entity structures. This is helpful for validation, controlling what data is returned, and keeping the API contract stable even if the database model changes.

### 5) Validation
Requests are validated using `spring-boot-starter-validation` and `@Valid`. This ensures invalid data is rejected early, improves error handling, and keeps the service layer focused on business logic rather than input checking.

### 6) JPA Entity Mapping
Entities in the `models` package are mapped to database tables using JPA annotations. This provides a clean object model while Hibernate handles SQL generation and persistence.

### 7) Repository Pattern
Repositories extend `JpaRepository`, giving a clean API for CRUD operations and custom queries. This avoids writing raw SQL for common operations and keeps database access consistent.

### 8) Transaction Management
Methods that perform multiple database updates are annotated with `@Transactional`. This guarantees that either all operations succeed together or none are applied, preventing partial updates.

### 9) Security with JWT
Authentication is stateless. Users log in and receive a JWT token, which is sent on each request. A filter validates the token, builds the authenticated user context, and protects private endpoints.

### 10) CORS Configuration
The backend explicitly allows requests from the Angular frontend. This is required for browser-based clients to call the API across different origins.

### 11) File Upload Handling
Multipart uploads are handled by Spring. File size limits are configured in `application.properties`, and uploaded files are stored on disk and served from `/uploads`.

## Angular Principles

### 1) Component-Based UI
The frontend is built from Angular components. Each screen or UI section is its own component, with a template, styles, and a TypeScript class.

### 2) Modules and Routing
Features are organized into modules. The router maps URLs to components, enabling clean navigation and lazy-loading patterns if needed.

### 3) Services for API Calls
HTTP calls are centralized in services. This keeps components clean and allows API logic to be reused across multiple views.

### 4) Dependency Injection
Angular injects services into components and other services through constructors. This keeps the app modular and simplifies testing.

### 5) Interceptors
The JWT token is attached to outgoing requests using an HTTP interceptor. This avoids repeating auth logic in every service method.

### 6) Guards
Guards restrict navigation to protected routes. They check authentication and roles before a user can access certain pages.

### 7) RxJS and Observables
Angular uses observables for async flows like HTTP requests. This makes it easy to handle success, errors, and UI updates.

### 8) Template Binding
Data from the component is bound to the HTML template using Angular bindings. Event handlers respond to user actions like clicks and form submissions.

### 9) Separation of Concerns
UI and state live in components, while networking and shared logic live in services. This separation keeps the code easier to extend and debug.

## Docker Compose Notes

Database persistence uses the named volume `mysql_data` in `blogger/docker-compose.yml`. Your data will persist across restarts as long as you do not remove the volume.

Use the run script:
```
./ScriptRunApplication.sh
```

If you want a fresh database, run:
```
./ScriptRunApplication.sh --reset-db
```
## 🔐 AuthController — Authentication & Registration
**Base URL:** `/api/auth`



| Method | Endpoint | Description |
|--------|-----------|-------------|
| **POST** | `/register` | Register a new user |
| **POST** | `/login` | Login user and return JWT token |
| **GET** | `/me` | Get current logged-in user info |
| **POST** | `/logout` | Logout (optional in JWT) |

---

## 👤 UserController — User Profiles & Subscriptions
**Base URL:** `/api/users`

| Method | Endpoint | Description |
|--------|-----------|-------------|
| **GET** | `/` | Get all users (Admin only) |
| **GET** | `/{id}` | Get specific user profile |
| **PUT** | `/{id}` | Update user info |
| **DELETE** | `/{id}` | Delete user (Admin only) |
| **GET** | `/{id}/posts` | Get all posts by user |
| **POST** | `/{id}/subscribe` | Follow a user |
| **DELETE** | `/{id}/unsubscribe` | Unfollow a user |
| **GET** | `/subscriptions` | Get current user's subscriptions |
| **GET** | `/followers` | Get current user's followers |

---

## 📝 PostController — Posts Management
**Base URL:** `/api/posts`

| Method | Endpoint | Description |
|--------|-----------|-------------|
| **GET** | `/` | Get all posts (feed) |
| **GET** | `/{id}` | Get one post |
| **POST** | `/` | Create new post |
| **PUT** | `/{id}` | Update post |
| **DELETE** | `/{id}` | Delete post |
| **POST** | `/{id}/like` | Like a post |
| **DELETE** | `/{id}/unlike` | Unlike a post |
| **GET** | `/{id}/likes` | Get all users who liked post |

---

## 💬 CommentController — Comments System
**Base URL:** `/api/posts/{postId}/comments`

| Method | Endpoint | Description |
|--------|-----------|-------------|
| **GET** | `/` | Get all comments for a post |
| **POST** | `/` | Add a comment |
| **PUT** | `/{commentId}` | Edit a comment |
| **DELETE** | `/{commentId}` | Delete a comment |

---

## 🚨 ReportController — Reports & Abuse System
**Base URL:** `/api/reports`

| Method | Endpoint | Description |
|--------|-----------|-------------|
| **POST** | `/` | Create a report (against user or post) |
| **GET** | `/` | Get all reports (Admin only) |
| **GET** | `/{id}` | Get report details |
| **DELETE** | `/{id}` | Delete report (Admin only) |

--- 

## 🛠️ AdminController — Admin Tools
**Base URL:** `/api/admin`

| Method | Endpoint | Description |
|--------|-----------|-------------|
| **GET** | `/users` | Get all users |
| **GET** | `/posts` | Get all posts |
| **GET** | `/reports` | Get all reports |
| **DELETE** | `/users/{id}` | Delete or ban a user |
| **DELETE** | `/posts/{id}` | Delete inappropriate post |
| **PUT** | `/users/{id}/ban` | Ban a user |
| **PUT** | `/users/{id}/unban` | Unban a user |

---

## 🔔 NotificationController — Notifications System
**Base URL:** `/api/notifications`

| Method | Endpoint | Description |
|--------|-----------|-------------|
| **GET** | `/` | Get all notifications for current user |
| **PUT** | `/{id}/read` | Mark notification as read |
| **DELETE** | `/{id}` | Delete a notification |

---

## 🖼️ MediaController — Media Uploads (Images/Videos)
**Base URL:** `/api/media`

| Method | Endpoint | Description |
|--------|-----------|-------------|
| **POST** | `/upload` | Upload a file (image/video) |
| **GET** | `/view/{filename}` | View a file |
| **DELETE** | `/delete/{filename}` | Delete a file |

---

## 🗂️ Summary by Controller

| Controller | Purpose | Example Endpoints |
|-------------|----------|-------------------|
| **AuthController** | Authentication | `/api/auth/register`, `/api/auth/login` |
| **UserController** | Profiles & Subscriptions | `/api/users/{id}`, `/api/users/{id}/subscribe` |
| **PostController** | Posts CRUD | `/api/posts`, `/api/posts/{id}/like` |
| **CommentController** | Comments CRUD | `/api/posts/{postId}/comments` |
| **ReportController** | Reports Management | `/api/reports` |
| **AdminController** | Admin Operations | `/api/admin/users`, `/api/admin/reports` |
| **NotificationController** | Notifications | `/api/notifications` |
| **MediaController** | File Uploads | `/api/media/upload` |

---
/*
angular materiel 
/*

How to run programme.

./mvnw spring-boot:run
ng serve --open

---

01blog/
│
├── src/
│ ├── main/
│ │ ├── java/com/example/blog/
│ │ │ ├── controllers/
│ │ │ │ ├── AuthController.java
│ │ │ │ ├── UserController.java
│ │ │ │ ├── PostController.java
│ │ │ │ ├── CommentController.java
│ │ │ │ ├── ReportController.java
│ │ │ │ └── AdminController.java
│ │ │ │
│ │ │ ├── models/
│ │ │ │ ├── User.java
│ │ │ │ ├── Role.java
│ │ │ │ ├── Post.java
│ │ │ │ ├── Comment.java
│ │ │ │ ├── Report.java
│ │ │ │ └── Subscription.java
│ │ │ │
│ │ │ ├── repositories/
│ │ │ │ ├── UserRepository.java
│ │ │ │ ├── PostRepository.java
│ │ │ │ ├── CommentRepository.java
│ │ │ │ ├── ReportRepository.java
│ │ │ │ └── SubscriptionRepository.java
│ │ │ │
│ │ │ ├── services/
│ │ │ │ ├── UserService.java
│ │ │ │ ├── PostService.java
│ │ │ │ ├── CommentService.java
│ │ │ │ ├── ReportService.java
│ │ │ │ ├── SubscriptionService.java
│ │ │ │ └── NotificationService.java
│ │ │ │
│ │ │ ├── security/
│ │ │ │ ├── JwtUtil.java
│ │ │ │ ├── JwtFilter.java
│ │ │ │ ├── SecurityConfig.java
│ │ │ │ └── CustomUserDetailsService.java
│ │ │ │
│ │ │ ├── dto/
│ │ │ │ ├── LoginRequest.java
│ │ │ │ ├── RegisterRequest.java
│ │ │ │ ├── PostRequest.java
│ │ │ │ ├── CommentRequest.java
│ │ │ │ └── ReportRequest.java
│ │ │ │
│ │ │ ├── exceptions/
│ │ │ │ ├── GlobalExceptionHandler.java
│ │ │ │ ├── ResourceNotFoundException.java
│ │ │ │ └── UnauthorizedException.java
│ │ │ │
│ │ │ ├── config/
│ │ │ │ └── CorsConfig.java
│ │ │ │
│ │ │ └── BloggerApplication.java
│ │ │
│ │ └── resources/
│ │ ├── application.properties
│ │ └── static/
│ │
│ └── test/
│ └── java/com/example/blog/
│ ├── BlogApplicationTests.java
│ └── services/
│ └── PostServiceTests.java
│
├── pom.xml
└── README.md

---

blog-frontend/
│
├── src/
│ ├── app/
│ │ ├── core/ # Application core (auth, interceptors, guards, services globaux)
│ │ │ ├── interceptors/
│ │ │ │ └── jwt.interceptor.ts
│ │ │ ├── guards/
│ │ │ │ └── auth.guard.ts
│ │ │ ├── services/
│ │ │ │ ├── auth.service.ts
│ │ │ │ ├── user.service.ts
│ │ │ │ ├── post.service.ts
│ │ │ │ ├── comment.service.ts
│ │ │ │ ├── report.service.ts
│ │ │ │ └── subscription.service.ts
│ │ │ ├── models/
│ │ │ │ ├── user.model.ts
│ │ │ │ ├── post.model.ts
│ │ │ │ ├── comment.model.ts
│ │ │ │ ├── report.model.ts
│ │ │ │ └── subscription.model.ts
│ │ │ └── core.module.ts
│ │ │
│ │ ├── shared/ # Shared reusable components & utilities
│ │ │ ├── components/
│ │ │ │ ├── navbar/
│ │ │ │ │ ├── navbar.component.ts
│ │ │ │ │ ├── navbar.component.html
│ │ │ │ │ └── navbar.component.css
│ │ │ │ └── footer/
│ │ │ │ ├── footer.component.ts
│ │ │ │ ├── footer.component.html
│ │ │ │ └── footer.component.css
│ │ │ └── shared.module.ts
│ │ │
│ │ ├── features/ # Modules for each domain (Auth, Posts, Users, etc.)
│ │ │ ├── auth/
│ │ │ │ ├── login/
│ │ │ │ │ ├── login.component.ts
│ │ │ │ │ └── login.component.html
│ │ │ │ ├── register/
│ │ │ │ │ ├── register.component.ts
│ │ │ │ │ └── register.component.html
│ │ │ │ ├── auth-routing.module.ts
│ │ │ │ └── auth.module.ts
│ │ │ │
│ │ │ ├── posts/
│ │ │ │ ├── list/
│ │ │ │ │ ├── post-list.component.ts
│ │ │ │ │ └── post-list.component.html
│ │ │ │ ├── details/
│ │ │ │ │ ├── post-details.component.ts
│ │ │ │ │ └── post-details.component.html
│ │ │ │ ├── create/
│ │ │ │ │ ├── post-create.component.ts
│ │ │ │ │ └── post-create.component.html
│ │ │ │ ├── posts-routing.module.ts
│ │ │ │ └── posts.module.ts
│ │ │ │
│ │ │ ├── admin/
│ │ │ │ ├── dashboard/
│ │ │ │ │ ├── admin-dashboard.component.ts
│ │ │ │ │ └── admin-dashboard.component.html
│ │ │ │ └── admin.module.ts
│ │ │ │
│ │ │ └── users/
│ │ │ ├── profile/
│ │ │ │ ├── user-profile.component.ts
│ │ │ │ └── user-profile.component.html
│ │ │ ├── edit/
│ │ │ │ ├── user-edit.component.ts
│ │ │ │ └── user-edit.component.html
│ │ │ ├── users-routing.module.ts
│ │ │ └── users.module.ts
│ │ │
│ │ ├── app-routing.module.ts # Main routing
│ │ ├── app.component.ts
│ │ ├── app.component.html
│ │ └── app.module.ts
│ │
│ └── assets/
│ ├── images/
│ ├── css/
│ └── icons/
│
├── angular.json
├── package.json
├── tsconfig.json
└── README.md

---

## 🔐 AuthController — Authentication & Registration

**Base URL:** `/api/auth`

| Method   | Endpoint    | Description                     |
| -------- | ----------- | ------------------------------- |
| **POST** | `/register` | Register a new user             |
| **POST** | `/login`    | Login user and return JWT token |
| **GET**  | `/me`       | Get current logged-in user info |
| **POST** | `/logout`   | Logout (optional in JWT)        |

---

## 👤 UserController — User Profiles & Subscriptions

**Base URL:** `/api/users`

| Method     | Endpoint            | Description                      |
| ---------- | ------------------- | -------------------------------- |
| **GET**    | `/`                 | Get all users (Admin only)       |
| **GET**    | `/{id}`             | Get specific user profile        |
| **PUT**    | `/{id}`             | Update user info                 |
| **DELETE** | `/{id}`             | Delete user (Admin only)         |
| **GET**    | `/{id}/posts`       | Get all posts by user            |
| **POST**   | `/{id}/subscribe`   | Follow a user                    |
| **DELETE** | `/{id}/unsubscribe` | Unfollow a user                  |
| **GET**    | `/subscriptions`    | Get current user's subscriptions |
| **GET**    | `/followers`        | Get current user's followers     |

---

## 📝 PostController — Posts Management

**Base URL:** `/api/posts`

| Method     | Endpoint       | Description                  |
| ---------- | -------------- | ---------------------------- |
| **GET**    | `/`            | Get all posts (feed)         |
| **GET**    | `/{id}`        | Get one post                 |
| **POST**   | `/`            | Create new post              |
| **PUT**    | `/{id}`        | Update post                  |
| **DELETE** | `/{id}`        | Delete post                  |
| **POST**   | `/{id}/like`   | Like a post                  |
| **DELETE** | `/{id}/unlike` | Unlike a post                |
| **GET**    | `/{id}/likes`  | Get all users who liked post |

---

## 💬 CommentController — Comments System

**Base URL:** `/api/posts/{postId}/comments`

| Method     | Endpoint       | Description                 |
| ---------- | -------------- | --------------------------- |
| **GET**    | `/`            | Get all comments for a post |
| **POST**   | `/`            | Add a comment               |
| **PUT**    | `/{commentId}` | Edit a comment              |
| **DELETE** | `/{commentId}` | Delete a comment            |

---

## 🚨 ReportController — Reports & Abuse System

**Base URL:** `/api/reports`

| Method     | Endpoint | Description                            |
| ---------- | -------- | -------------------------------------- |
| **POST**   | `/`      | Create a report (against user or post) |
| **GET**    | `/`      | Get all reports (Admin only)           |
| **GET**    | `/{id}`  | Get report details                     |
| **DELETE** | `/{id}`  | Delete report (Admin only)             |

---

## 🛠️ AdminController — Admin Tools

**Base URL:** `/api/admin`

| Method     | Endpoint            | Description               |
| ---------- | ------------------- | ------------------------- |
| **GET**    | `/users`            | Get all users             |
| **GET**    | `/posts`            | Get all posts             |
| **GET**    | `/reports`          | Get all reports           |
| **DELETE** | `/users/{id}`       | Delete or ban a user      |
| **DELETE** | `/posts/{id}`       | Delete inappropriate post |
| **PUT**    | `/users/{id}/ban`   | Ban a user                |
| **PUT**    | `/users/{id}/unban` | Unban a user              |

---

## 🔔 NotificationController — Notifications System

**Base URL:** `/api/notifications`

| Method     | Endpoint     | Description                            |
| ---------- | ------------ | -------------------------------------- |
| **GET**    | `/`          | Get all notifications for current user |
| **PUT**    | `/{id}/read` | Mark notification as read              |
| **DELETE** | `/{id}`      | Delete a notification                  |

---

## 🖼️ MediaController — Media Uploads (Images/Videos)

**Base URL:** `/api/media`

| Method     | Endpoint             | Description                 |
| ---------- | -------------------- | --------------------------- |
| **POST**   | `/upload`            | Upload a file (image/video) |
| **GET**    | `/view/{filename}`   | View a file                 |
| **DELETE** | `/delete/{filename}` | Delete a file               |

---

## 🗂️ Summary by Controller

| Controller                 | Purpose                  | Example Endpoints                              |
| -------------------------- | ------------------------ | ---------------------------------------------- |
| **AuthController**         | Authentication           | `/api/auth/register`, `/api/auth/login`        |
| **UserController**         | Profiles & Subscriptions | `/api/users/{id}`, `/api/users/{id}/subscribe` |
| **PostController**         | Posts CRUD               | `/api/posts`, `/api/posts/{id}/like`           |
| **CommentController**      | Comments CRUD            | `/api/posts/{postId}/comments`                 |
| **ReportController**       | Reports Management       | `/api/reports`                                 |
| **AdminController**        | Admin Operations         | `/api/admin/users`, `/api/admin/reports`       |
| **NotificationController** | Notifications            | `/api/notifications`                           |
| **MediaController**        | File Uploads             | `/api/media/upload`                            |

---

/_
angular materiel
/_

---

## loguque of report post

1. FRONTEND (Angular):

   - User clicks "Report" button
   - Opens modal with form
   - Sends JSON to backend

2. BACKEND FLOW:
   ┌─────────────────────────────────────┐
   │ 1. Request arrives at Controller │
   │ - URL: POST /auth/reports │
   │ - Body: {postId: 22, reason: "SPAM"}│
   └──────────────────┬──────────────────┘
   ↓
   ┌─────────────────────────────────────┐
   │ 2. Controller validates & forwards │
   │ - Checks authentication │
   │ - Converts JSON to DTO │
   │ - Calls reportService.createReport()│
   └──────────────────┬──────────────────┘
   ↓
   ┌─────────────────────────────────────┐
   │ 3. Service applies business rules │
   │ - Rule 1: Post must exist │
   │ - Rule 2: No duplicate reports │
   │ - Rule 3: Valid reason │
   │ - Creates Report entity │
   └──────────────────┬──────────────────┘
   ↓
   ┌─────────────────────────────────────┐
   │ 4. Repository saves to database │
   │ - INSERT INTO reports (...) │
   │ - Returns saved entity with ID │
   └──────────────────┬──────────────────┘
   ↓
   ┌─────────────────────────────────────┐
   │ 5. Service converts to Response DTO │
   │ - Entity → DTO transformation │
   │ - Adds calculated fields │
   └──────────────────┬──────────────────┘
   ↓
   ┌─────────────────────────────────────┐
   │ 6. Controller sends HTTP response │
   │ - Status: 201 Created │
   │ - Body: Report DTO │
   └──────────────────┬──────────────────┘
   ↓
   ┌─────────────────────────────────────┐
   │ 7. Frontend receives response │
   │ - Shows success message │
   │ - Updates UI │
   └─────────────────────────────────────┘
