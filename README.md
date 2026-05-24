# FerreSys

<<<<<<< HEAD
FerreSys is a Point of Sale (POS) and Inventory Management System with role-based access control (RBAC), user authentication, and a modern web interface. The project is split into two main components: a Spring Boot backend API and an Angular frontend web application.

## Project Structure

This repository is organized into two main directories:

- `/ferresys-api`: The backend REST API built with Java and Spring Boot.
- `/ferresys-web`: The frontend Single Page Application (SPA) built with Angular.
=======
FerreSys es un sistema de punto de venta (TPV) y gestión de inventario con control de acceso basado en roles (RBAC), autenticación de usuarios y una interfaz web moderna. El proyecto se divide en dos componentes principales: una API backend de Spring Boot y una aplicación web frontend de Angular.

## Estructura del proyecto

Este repositorio está organizado en dos directorios principales:

- `/ferresys-api`: La API REST backend desarrollada con Java y Spring Boot.

- `/ferresys-web`: La aplicación web de una sola página (SPA) frontend desarrollada con Angular.
>>>>>>> 3990ce29573eb3018050bc771c91cd332eff6ed8

---

## 🛠 Backend (`ferresys-api`)

<<<<<<< HEAD
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
=======
El backend es una API RESTful robusta diseñada para gestionar la lógica de negocio, la base de datos y la seguridad.

### Tecnologías y Stack
- **Lenguaje**: Java 17
- **Framework**: Spring Boot 4.x
- **Base de datos**: PostgreSQL (con H2 para pruebas)
- **Acceso a datos**: Spring Data JPA
- **Migraciones de base de datos**: Flyway
- **Seguridad**: Spring Security con JWT (JSON Web Tokens) para autenticación y RBAC.

- **Documentación de la API**: SpringDoc OpenAPI (Swagger)
- **Utilidades**: Lombok

### Características principales
- **Autenticación JWT**: Inicio de sesión seguro y generación de tokens.

- **Control de acceso basado en roles (RBAC)**: Control de acceso granular para diferentes endpoints según los roles de usuario (p. ej., Administrador, Consumidor final).

- **Gestión global de errores**: Respuestas de error JSON estandarizadas.

- **Limitación de velocidad**: Protección contra el abuso en endpoints públicos.
- **Inserción de datos en la base de datos**: Creación automática de usuarios predeterminados (AdminSeeder).

### Cómo ejecutar localmente

1. **Requisitos previos**:

- Java 17 instalado.

- PostgreSQL instalado y en ejecución (o Docker).

- Maven instalado.

2. **Configuración de la base de datos**:

Crea una base de datos PostgreSQL y actualiza el archivo `application.properties` (o `.yml`) en `src/main/resources` con tus credenciales de base de datos.

3. **Iniciar la aplicación**:

Navega al directorio `ferresys-api` y ejecuta:

```bash

cd ferresys-api

mvn spring-boot:run

```

El backend normalmente se iniciará en `http://localhost:8080`. Puedes acceder a la documentación de Swagger UI en `http://localhost:8080/swagger-ui.html` (dependiendo de la configuración).
>>>>>>> 3990ce29573eb3018050bc771c91cd332eff6ed8

---

## 💻 Frontend (`ferresys-web`)

<<<<<<< HEAD
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
=======
El frontend es una aplicación web responsiva diseñada para facilitar su uso, con interfaces intuitivas para el punto de venta, el inventario y la gestión de usuarios.

### Tecnologías y Stack
- **Framework**: Angular 20.x
- **Estilo**: Bootstrap 5, CSS personalizado.

- **Alertas y Notificaciones**: SweetAlert2
- **Autenticación**: Gestión de tokens JWT (`jwt-decode`)

### Características Principales
- **Protección de Rutas**: Protección del lado del cliente para las rutas según el estado de autenticación y los roles de usuario.

- **Interfaz de TPV Dinámica**: Diseño moderno y organizado para la selección de productos y la gestión del carrito.

- **Gestión de Errores**: Alertas fáciles de usar (a través de SweetAlert2) sincronizadas con las respuestas de error del backend.
- **Diseños centralizados**: Interfaz de usuario profesional para inicio de sesión, registro y paneles de administración.

### Cómo ejecutar localmente

1. **Requisitos previos**:

- Node.js instalado.

- Angular CLI instalado globalmente (`npm install -g @angular/cli`).

2. **Instalar dependencias**:

Navegue al directorio `ferresys-web` e instale los paquetes necesarios:

```bash

cd ferresys-web

npm install

```

3. **Iniciar el servidor de desarrollo**:

Ejecute el siguiente comando para iniciar el servidor Angular:

```bash

npm start

```

o

```bash

ng serve

```
Navegue a `http://localhost:4200/` en su navegador. La aplicación se recargará automáticamente si modifica alguno de los archivos fuente.

---

## 🔒 Resumen de seguridad

FerreSys implementa una estrategia de seguridad integral en ambos extremos:
- El **backend** valida todas las solicitudes entrantes mediante un filtro JWT personalizado, aplicando límites de velocidad y verificando los roles.

- El **frontend** gestiona el almacenamiento de tokens, decodifica los roles para la lógica de renderizado de la interfaz de usuario e intercepta las solicitudes HTTP para adjuntar el token Bearer, gestionando adecuadamente las expiraciones.
>>>>>>> 3990ce29573eb3018050bc771c91cd332eff6ed8
