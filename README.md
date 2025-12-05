# Visual Vault Project

## Descripción
Visual Vault es un sistema de gestión de videos desarrollado con Spring Boot. Permite a los usuarios autenticados gestionar su propia colección de videos, crear listas de reproducción, y administrar su perfil. Incluye funcionalidades avanzadas como el seguimiento de videos vistos y autenticación segura mediante JWT.

## Tecnologías Utilizadas
*   **Java**: 17
*   **Framework**: Spring Boot 3.5.6
*   **Seguridad**: Spring Security, JWT (JSON Web Tokens)
*   **Base de Datos**: H2 Database (En memoria)
*   **Documentación API**: OpenAPI / Swagger UI
*   **Herramientas**: Maven, Lombok

## Requisitos Previos
*   Java Development Kit (JDK) 17 o superior.
*   Maven instalado (o usar el wrapper `mvnw` incluido).

## Instalación y Ejecución

1.  **Clonar el repositorio:**
    ```bash
    git clone <url-del-repositorio>
    cd visual_vault_project
    ```

2.  **Compilar el proyecto:**
    ```bash
    ./mvnw clean install
    ```

3.  **Ejecutar la aplicación:**
    ```bash
    ./mvnw spring-boot:run
    ```

La aplicación se iniciará en el puerto `9000`.

## Configuración
La configuración principal se encuentra en `src/main/resources/application.properties`.

*   **Puerto del Servidor**: `9000`
*   **Base de Datos**: H2 en memoria (`jdbc:h2:mem:visualVaultProject`)
*   **Consola H2**: Habilitada en `/h2-console`
    *   **JDBC URL**: `jdbc:h2:mem:visualVaultProject`
    *   **Usuario**: `sa`
    *   **Contraseña**: `contrasena`

## Documentación de la API
La documentación interactiva está disponible en Swagger UI: (http://localhost:9000/swagger-ui/index.html#/)

### Autenticación (`/api/auth`)
Endpoints para gestión de sesiones y registro.
*   **POST** `/api/auth/register`: Registrar un nuevo usuario.
    *   *Body*: `{ "username": "...", "email": "...", "password": "..." }`
*   **POST** `/api/auth/login`: Iniciar sesión y obtener token JWT.
    *   *Body*: `{ "username": "...", "password": "..." }`
*   **GET** `/api/auth/me`: Obtener datos del usuario actual (requiere token).
*   **POST** `/api/auth/change-password`: Cambiar la contraseña del usuario actual.
    *   *Body*: `{ "currentPassword": "...", "newPassword": "..." }`

### Videos (`/api/videos`)
Gestión de la biblioteca de videos. Requiere autenticación.
*   **GET** `/api/videos/list`: Listar todos los videos del usuario autenticado.
*   **GET** `/api/videos/{id}`: Obtener detalles de un video específico.
*   **POST** `/api/videos/send`: Crear un nuevo video.
    *   *Body*: `{ "titulo": "...", "url": "...", "categoria": "...", ... }`
*   **PUT** `/api/videos/{id}`: Actualizar datos de un video.
*   **DELETE** `/api/videos/{id}`: Eliminar un video.
*   **PATCH** `/api/videos/{id}/toggle-visto`: Marcar/Desmarcar un video como visto.
    *   *Funcionalidad*: Invierte el estado actual de la propiedad `visto`.
*   **GET** `/api/videos/vistos`: Listar solo los videos marcados como vistos.
*   **GET** `/api/videos/no-vistos`: Listar solo los videos pendientes de ver.

### Listas de Reproducción (`/api/listas`)
Gestión de listas personalizadas de videos.
*   **POST** `/api/listas`: Crear una nueva lista.
*   **GET** `/api/listas/usuario/{usuarioId}`: Obtener todas las listas de un usuario.
*   **POST** `/api/listas/{listaId}/videos/{videoId}`: Añadir un video a una lista.
*   **DELETE** `/api/listas/{listaId}/videos/{videoId}`: Eliminar un video de una lista.

### Usuarios (`/api/usuarios`)
Gestión administrativa de usuarios.
*   **GET** `/api/usuarios/list`: Listar todos los usuarios registrados.
*   **GET** `/api/usuarios/{id}`: Obtener usuario por ID.
*   **POST** `/api/usuarios`: Crear un usuario manualmente (Admin).
*   **PUT** `/api/usuarios/{id}`: Actualizar datos de un usuario.
*   **DELETE** `/api/usuarios/{id}`: Eliminar usuario por ID.
*   **DELETE** `/api/usuarios/username/{username}`: Eliminar usuario por nombre de usuario.

## Autenticación y Seguridad
El sistema utiliza **JWT (JSON Web Tokens)**. Para acceder a los endpoints protegidos, debes incluir el token obtenido en el login en la cabecera de la petición:

```http
Authorization: Bearer <tu_token_jwt>
```


