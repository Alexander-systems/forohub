# ForoHub API REST

Este proyecto implementa una API REST para ForoHub, una plataforma tipo foro donde los usuarios pueden interactuar creando y gestionando tópicos de discusión, autenticarse y asignarse roles. La aplicación está construida con Spring Boot 3, Spring Security y utiliza JWT para la autenticación.

## 🚀 Tecnologías Utilizadas

* **Java 17**
* **Spring Boot 3.x**
* **Spring Data JPA**
* **Spring Security**
* **JWT (JSON Web Tokens)**
* **MySQL** (Base de datos)
* **Flyway** (Migraciones de base de datos)
* **Lombok**
* **Maven**

## ✨ Características

* **Autenticación de Usuarios**: Los usuarios pueden registrarse e iniciar sesión para obtener un token JWT.
* **Gestión de Tópicos**:
    * Crear nuevos tópicos.
    * Listar todos los tópicos (con paginación).
    * Buscar tópicos por curso y año.
    * Detallar un tópico específico por ID.
    * Actualizar tópicos existentes.
    * Eliminar tópicos.
* **Roles y Autorización**:
    * Los usuarios pueden tener roles (`USUARIO`, `ADMIN`).
    * Acceso controlado a los endpoints basado en roles (ej. solo `USUARIO` puede crear/actualizar tópicos, solo `ADMIN` puede eliminar tópicos).
* **Manejo de Errores Global**: Gestión centralizada de excepciones para respuestas de error consistentes.
* **Validación de Datos**: Validación de entrada en los DTOs utilizando `jakarta.validation`.
* **Migraciones de Base de Datos**: Uso de Flyway para gestionar el esquema de la base de datos de forma versionada.

## ⚙️ Configuración del Entorno

### Requisitos Previos

* Java Development Kit (JDK) 17 o superior
* Maven
* Servidor MySQL (o Docker para ejecutar MySQL)

### Pasos de Configuración

1.  **Clonar el Repositorio:**
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    cd forohub
    ```

2.  **Configurar la Base de Datos MySQL:**
    * Asegúrate de tener un servidor MySQL en ejecución.
    * Crea una base de datos llamada `forohub_db`.
        ```sql
        CREATE DATABASE forohub_db;
        ```
    * Actualiza las credenciales de la base de datos en `src/main/resources/application.properties` si son diferentes a las predeterminadas (`root`/`admin123`).

        ```properties
        spring.datasource.url=jdbc:mysql://localhost/forohub_db
        spring.datasource.username=root
        spring.datasource.password=admin123
        ```
    * Las migraciones de Flyway (`db/migration/V1__create_tables.sql`) se ejecutarán automáticamente al iniciar la aplicación, creando las tablas y poblando los datos iniciales.

3.  **Configurar el Secreto JWT:**
    * En `src/main/resources/application.properties`, **cambia el valor de `api.security.secret`** por una cadena más robusta y segura.

        ```properties
        api.security.secret=TU_SECRETO_JWT_SEGURO_AQUI # ¡IMPORTANTE: CAMBIA ESTO!
        api.security.jwt.zone-offset=-05:00
        ```

4.  **Compilar y Ejecutar la Aplicación:**
    Puedes ejecutar la aplicación directamente desde tu IDE (ej. IntelliJ IDEA, Eclipse) o usando Maven:

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    La aplicación se iniciará en `http://localhost:8080`.

## 📚 Endpoints de la API

A continuación, se detallan los principales endpoints disponibles.
**Todos los `POST`,`GET`, `PUT` y `DELETE` requieren un token JWT válido en el encabezado `Authorization: Bearer <token>`.**

### Autenticación

* **`POST /login`**
    * **Descripción**: Autentica a un usuario y devuelve un token JWT.
    * **Cuerpo de la Solicitud (JSON)**:
        ```json
        {
            "correoElectronico": "usuario@example.com",
            "contrasena": "tu_contrasena"
        }
        ```
    * **Respuesta (JSON)**:
        ```json
        {
            "jwToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        }
        ```

### Tópicos

* **`POST /topicos`**
    * **Descripción**: Registra un nuevo tópico. Requiere rol `USUARIO`.
    * **Cuerpo de la Solicitud (JSON)**:
        ```json
        {
            "titulo": "Mi nuevo tópico de Spring",
            "mensaje": "Quiero discutir sobre las novedades de Spring 6.",
            "autorId": 1,
            "cursoId": 1
        }
        ```
    * **Respuesta (JSON)**: `HTTP 201 Created` con los detalles del tópico creado y el encabezado `Location`.

* **`GET /topicos`**
    * **Descripción**: Lista todos los tópicos con paginación. No requiere autenticación.
    * **Parámetros de consulta (Opcional)**:
        * `page`: Número de página (0-indexed, default 0).
        * `size`: Tamaño de la página (default 10).
        * `sort`: Campo por el que ordenar (ej. `fechaCreacion,desc`).
    * **Respuesta (JSON)**: `HTTP 200 OK` con un objeto `Page` de `DatosListadoTopico`.

* **`GET /topicos/buscar`**
    * **Descripción**: Busca tópicos por nombre de curso y/o año de creación. Si se usa uno, el otro es obligatorio. No requiere autenticación.
    * **Parámetros de consulta**:
        * `nombreCurso`: Nombre del curso (ej. "Desarrollo Web Fullstack").
        * `ano`: Año de creación (ej. 2024).
    * **Ejemplo**: `GET /topicos/buscar?nombreCurso=Desarrollo%20Web%20Fullstack&ano=2024`
    * **Respuesta (JSON)**: `HTTP 200 OK` con un objeto `Page` de `DatosListadoTopico`.

* **`GET /topicos/{id}`**
    * **Descripción**: Detalla un tópico específico por su ID. No requiere autenticación.
    * **Ejemplo**: `GET /topicos/1`
    * **Respuesta (JSON)**: `HTTP 200 OK` con los `DatosRespuestaTopico` del tópico.

* **`PUT /topicos/{id}`**
    * **Descripción**: Actualiza un tópico existente. Requiere rol `USUARIO`.
    * **Ejemplo**: `PUT /topicos/1`
    * **Cuerpo de la Solicitud (JSON)**: Los campos son opcionales para la actualización.
        ```json
        {
            "titulo": "Nuevo título del tópico",
            "mensaje": "Mensaje actualizado.",
            "status": "RESUELTO"
        }
        ```
    * **Respuesta (JSON)**: `HTTP 200 OK` con los `DatosRespuestaTopico` del tópico actualizado.

* **`DELETE /topicos/{id}`**
    * **Descripción**: Elimina un tópico por su ID. Requiere rol `ADMIN`.
    * **Ejemplo**: `DELETE /topicos/1`
    * **Respuesta**: `HTTP 204 No Content` si la eliminación es exitosa.

