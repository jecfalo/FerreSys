# FerreSys

FerreSys es un sistema de punto de venta (TPV) y gestión de inventario con control de acceso basado en roles (RBAC), autenticación de usuarios y una interfaz web moderna. El proyecto se divide en dos componentes principales: una API backend de Spring Boot y una aplicación web frontend de Angular.

## Estructura del proyecto

Este repositorio está organizado en dos directorios principales:

- `/ferresys-api`: La API REST backend desarrollada con Java y Spring Boot.

- `/ferresys-web`: La aplicación web de una sola página (SPA) frontend desarrollada con Angular.

---

## 🛠 Backend (`ferresys-api`)

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

---

## 💻 Frontend (`ferresys-web`)

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
