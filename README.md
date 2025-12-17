How to run programme.

   ./mvnw spring-boot:run
ng serve --open
---

01blog/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/blog/
│   │   │   ├── controllers/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── PostController.java
│   │   │   │   ├── CommentController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   └── AdminController.java
│   │   │   │
│   │   │   ├── models/
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Post.java
│   │   │   │   ├── Comment.java
│   │   │   │   ├── Report.java
│   │   │   │   └── Subscription.java
│   │   │   │
│   │   │   ├── repositories/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── PostRepository.java
│   │   │   │   ├── CommentRepository.java
│   │   │   │   ├── ReportRepository.java
│   │   │   │   └── SubscriptionRepository.java
│   │   │   │
│   │   │   ├── services/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── PostService.java
│   │   │   │   ├── CommentService.java
│   │   │   │   ├── ReportService.java
│   │   │   │   ├── SubscriptionService.java
│   │   │   │   └── NotificationService.java
│   │   │   │
│   │   │   ├── security/
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtFilter.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── PostRequest.java
│   │   │   │   ├── CommentRequest.java
│   │   │   │   └── ReportRequest.java
│   │   │   │
│   │   │   ├── exceptions/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── UnauthorizedException.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   └── CorsConfig.java
│   │   │   │
│   │   │   └── BloggerApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │
│   └── test/
│       └── java/com/example/blog/
│           ├── BlogApplicationTests.java
│           └── services/
│               └── PostServiceTests.java
│
├── pom.xml
└── README.md

------------------------------------------------------------------

blog-frontend/
│
├── src/
│   ├── app/
│   │   ├── core/                         # Application core (auth, interceptors, guards, services globaux)
│   │   │   ├── interceptors/
│   │   │   │   └── jwt.interceptor.ts
│   │   │   ├── guards/
│   │   │   │   └── auth.guard.ts
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts
│   │   │   │   ├── user.service.ts
│   │   │   │   ├── post.service.ts
│   │   │   │   ├── comment.service.ts
│   │   │   │   ├── report.service.ts
│   │   │   │   └── subscription.service.ts
│   │   │   ├── models/
│   │   │   │   ├── user.model.ts
│   │   │   │   ├── post.model.ts
│   │   │   │   ├── comment.model.ts
│   │   │   │   ├── report.model.ts
│   │   │   │   └── subscription.model.ts
│   │   │   └── core.module.ts
│   │   │
│   │   ├── shared/                       # Shared reusable components & utilities
│   │   │   ├── components/
│   │   │   │   ├── navbar/
│   │   │   │   │   ├── navbar.component.ts
│   │   │   │   │   ├── navbar.component.html
│   │   │   │   │   └── navbar.component.css
│   │   │   │   └── footer/
│   │   │   │       ├── footer.component.ts
│   │   │   │       ├── footer.component.html
│   │   │   │       └── footer.component.css
│   │   │   └── shared.module.ts
│   │   │
│   │   ├── features/                     # Modules for each domain (Auth, Posts, Users, etc.)
│   │   │   ├── auth/
│   │   │   │   ├── login/
│   │   │   │   │   ├── login.component.ts
│   │   │   │   │   └── login.component.html
│   │   │   │   ├── register/
│   │   │   │   │   ├── register.component.ts
│   │   │   │   │   └── register.component.html
│   │   │   │   ├── auth-routing.module.ts
│   │   │   │   └── auth.module.ts
│   │   │   │
│   │   │   ├── posts/
│   │   │   │   ├── list/
│   │   │   │   │   ├── post-list.component.ts
│   │   │   │   │   └── post-list.component.html
│   │   │   │   ├── details/
│   │   │   │   │   ├── post-details.component.ts
│   │   │   │   │   └── post-details.component.html
│   │   │   │   ├── create/
│   │   │   │   │   ├── post-create.component.ts
│   │   │   │   │   └── post-create.component.html
│   │   │   │   ├── posts-routing.module.ts
│   │   │   │   └── posts.module.ts
│   │   │   │
│   │   │   ├── admin/
│   │   │   │   ├── dashboard/
│   │   │   │   │   ├── admin-dashboard.component.ts
│   │   │   │   │   └── admin-dashboard.component.html
│   │   │   │   └── admin.module.ts
│   │   │   │
│   │   │   └── users/
│   │   │       ├── profile/
│   │   │       │   ├── user-profile.component.ts
│   │   │       │   └── user-profile.component.html
│   │   │       ├── edit/
│   │   │       │   ├── user-edit.component.ts
│   │   │       │   └── user-edit.component.html
│   │   │       ├── users-routing.module.ts
│   │   │       └── users.module.ts
│   │   │
│   │   ├── app-routing.module.ts         # Main routing
│   │   ├── app.component.ts
│   │   ├── app.component.html
│   │   └── app.module.ts
│   │
│   └── assets/
│       ├── images/
│       ├── css/
│       └── icons/
│
├── angular.json
├── package.json
├── tsconfig.json
└── README.md

--------------------------------------

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
