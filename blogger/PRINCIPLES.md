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
