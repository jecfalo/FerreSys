# FerreSys

FerreSys is a Point of Sale (POS) and Inventory Management System with role-based access control (RBAC), user authentication, and a modern web interface. The project is split into two main components: a Spring Boot backend API and an Angular frontend web application.

## Project Structure

This repository is organized into two main directories:

- `/ferresys-api`: The backend REST API built with Java and Spring Boot.
- `/ferresys-web`: The frontend Single Page Application (SPA) built with Angular.

---

## 🛠 Backend (`ferresys-api`)

The backend is a robust RESTful API built to handle business logic, database management, and security.

### Technologies & Stack
- **Language**: Java 17
- **Framework**: Spring Boot 4.x
- **Database**: PostgreSQL (with H2 for testing)
- **Data Access**: Spring Data JPA
- **Database Migrations**: Flyway
- **Security**: Spring Security with JWT (JSON Web Tokens) for authentication and RBAC.
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Utilities**: Lombok

### Key Features
- **JWT Authentication**: Secure login and token generation.
- **Role-Based Access Control (RBAC)**: Fine-grained access control for different endpoints based on user roles (e.g., Admin, Final Consumer).
- **Global Error Handling**: Standardized JSON error responses.
- **Rate Limiting**: Protection against abuse on public endpoints.
- **Database Seeding**: Automatic creation of default users (AdminSeeder).

### How to Run Locally

1. **Prerequisites**:
   - Java 17 installed.
   - PostgreSQL installed and running (or Docker).
   - Maven installed.

2. **Database Configuration**:
   Create a PostgreSQL database and update the `application.properties` (or `.yml`) in `src/main/resources` with your database credentials.

3. **Start the application**:
   Navigate to the `ferresys-api` directory and run:
   ```bash
   cd ferresys-api
   mvn spring-boot:run
   ```
   The backend will typically start on `http://localhost:8080`. You can access the Swagger UI documentation at `http://localhost:8080/swagger-ui.html` (depending on configuration).

---

## 💻 Frontend (`ferresys-web`)

The frontend is a responsive web application designed for ease of use, providing intuitive interfaces for point of sale, inventory, and user management.

### Technologies & Stack
- **Framework**: Angular 20.x
- **Styling**: Bootstrap 5, custom CSS.
- **Alerts & Notifications**: SweetAlert2
- **Authentication**: JWT token handling (`jwt-decode`)

### Key Features
- **Route Guards**: Client-side protection of routes based on authentication status and user roles.
- **Dynamic POS Interface**: Modern and organized layout for product selection and cart management.
- **Error Handling**: User-friendly alerts (via SweetAlert2) synchronized with backend error responses.
- **Centralized Layouts**: Professional UI for login, registration, and administrative dashboards.

### How to Run Locally

1. **Prerequisites**:
   - Node.js installed.
   - Angular CLI installed globally (`npm install -g @angular/cli`).

2. **Install Dependencies**:
   Navigate to the `ferresys-web` directory and install the required packages:
   ```bash
   cd ferresys-web
   npm install
   ```

3. **Start the Development Server**:
   Run the following command to start the Angular server:
   ```bash
   npm start
   ```
   or
   ```bash
   ng serve
   ```
   Navigate to `http://localhost:4200/` in your browser. The app will automatically reload if you change any of the source files.

---

## 🔒 Security Summary

FerreSys implements a comprehensive security strategy across both ends:
- The **backend** validates all incoming requests via a custom JWT filter, applying rate limits and checking roles.
- The **frontend** handles token storage, decodes roles for UI rendering logic, and intercepts HTTP requests to attach the Bearer token, gracefully handling expirations.
