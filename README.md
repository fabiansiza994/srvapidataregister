# srvapidataregister

Servicio backend en Spring Boot para registro y gestión de datos (autenticación JWT, usuarios, empresas, notificaciones y utilidades de almacenamiento S3). Este README actualiza y amplía la documentación previa, conservando la sección útil sobre el modelo de seguridad.

Fecha: 2025-10-20

---

## Contenido
- Visión general
- Stack y requisitos
- Configuración (variables de entorno y properties)
- Cómo ejecutar (Maven, JAR, Docker)
- Scripts/Comandos útiles
- Estructura del proyecto
- Pruebas
- Seguridad (sección existente)
- Licencia

---

## Visión general
Aplicación Java 17 basada en Spring Boot 3 que expone APIs REST y utilidades:
- Autenticación y autorización con JWT y Spring Security.
- Persistencia con Spring Data JPA y PostgreSQL.
- Envío de correo (Spring Mail) y plantillas Thymeleaf.
- Almacenamiento de archivos en AWS S3.
- Redis (starter incluido) preparado para caché/cola.
- Utilidades: Apache Tika (detección MIME), Apache POI (Excel), Twilio SDK (SMS/WhatsApp).

---

## Stack y requisitos
- Lenguaje: Java 17
- Framework: Spring Boot 3.4.4
- Gestor de dependencias: Maven (mvn/mvnw)
- Base de datos: PostgreSQL (runtime)
- Otros servicios: AWS S3, SMTP, Redis, Twilio (opcionales según módulos)

Requisitos locales mínimos:
- Java 17 (JDK)
- Maven 3.9+ (o usar wrappers `mvnw`/`mvnw.cmd`)
- PostgreSQL (si ejecutas módulos que lo requieran)

---

## Configuración
Las propiedades se pueden definir vía `application.properties`/`application-<perfil>.properties` o variables de entorno equivalentes.

Perfil activo por defecto:
- `spring.profiles.active=local` (ver `src/main/resources/application.properties`).

Claves de configuración detectadas en el código (usa variables de entorno o properties):
- JWT
  - `jwt.secret` (secreto para firmar tokens)
  - `jwt.expiration.minutes` (minutos de expiración)
- Frontend
  - `front.url` (URL base del front para enlaces/notificaciones)
- Correo (Spring Mail)
  - `spring.mail.username` (remitente por defecto)
  - Otras típicas: `spring.mail.host`, `spring.mail.port`, `spring.mail.password`, `spring.mail.properties.*` (TODO: confirmar necesarias)
- AWS S3
  - `aws.s3.region`
  - `aws.s3.access-key`
  - `aws.s3.secret-key`
  - `aws.s3.bucket`
- Base de datos (PostgreSQL)
  - `spring.datasource.url` (p. ej. `jdbc:postgresql://localhost:5432/tu_db`)
  - `spring.datasource.username`
  - `spring.datasource.password`
  - `spring.jpa.hibernate.ddl-auto` (p. ej. `update`/`validate`) — TODO: confirmar política en este proyecto
- Redis (si se usa)
  - `spring.data.redis.host`, `spring.data.redis.port` — TODO: confirmar
- Twilio (si se usa)
  - Variables típicas: `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, remitente — TODO: confirmar mapeo a properties

Notas:
- Este repo no incluye `application-*.properties` con credenciales; configura tus valores localmente o vía variables de entorno del sistema.
- No se comparten secretos en el repositorio.

---

## Cómo ejecutar

Opción A) Maven (recomendado en desarrollo):
1. Compilar y ejecutar pruebas (si existen):
   - `./mvnw clean verify` (Linux/Mac)
   - `mvnw.cmd clean verify` (Windows)
2. Ejecutar la app:
   - `./mvnw spring-boot:run` (Linux/Mac)
   - `mvnw.cmd spring-boot:run` (Windows)

Opción B) JAR ejecutable:
1. Empaquetar: `./mvnw clean package -DskipTests`
2. Ejecutar:
   - `java -jar target/srvapidataregister-0.0.1-SNAPSHOT.jar`
   - Cambiar puerto con `-Dserver.port=8080` si lo necesitas.

Opción C) Docker:
1. Asegúrate de haber generado el JAR en `target/`.
2. Construir imagen: `docker build -t srvapidataregister:local .`
3. Ejecutar contenedor (Render usa la variable PORT; por defecto exponemos 10000):
   - `docker run -e PORT=10000 -p 10000:10000 --name dataregister srvapidataregister:local`
   - Pasa variables necesarias (`JWT`, `DB`, `S3`, `MAIL`, etc.) con `-e` o un archivo `--env-file`.

---

## Scripts / comandos útiles
- Compilar: `mvn clean compile`
- Empaquetar: `mvn clean package`
- Ejecutar: `mvn spring-boot:run`
- Tests: `mvn test`
- Formato estándar Maven Wrapper en Windows: `mvnw.cmd <goal>`

---

## Estructura del proyecto
- `src/main/java/com/fmsp/srvapidataregister/DataRegisterApplication.java` (entry point)
- `src/main/java/com/fmsp/srvapidataregister/modules/...` (módulos de dominio: users, clients, jobs, map, presence, notification, S3, pay, etc.)
- `src/main/java/com/fmsp/srvapidataregister/config/...` (configuración: S3, propiedades, etc.)
- `src/main/java/com/fmsp/srvapidataregister/core/...` (utilidades y payloads comunes)
- `src/main/resources/application.properties` (perfil activo por defecto)
- `src/main/resources/templates/...` (plantillas Thymeleaf, p. ej. `templates/mail`)
- `pom.xml` (dependencias y plugins)
- `Dockerfile` (ejecución en contenedor)

Árbol resumido:
- src/
  - main/
    - java/
      - com.fmsp.srvapidataregister/
        - DataRegisterApplication.java
        - config/
        - core/
        - modules/
    - resources/
      - application.properties
      - templates/
  - test/
    - java/ (TODO: agregar pruebas)

---

## Pruebas
- No se encontraron pruebas unitarias/integración en `src/test` con anotaciones `@Test` al momento de esta actualización.
- Ejecuta `mvn test` para cualquier prueba que agregues.
- TODO: añadir suite mínima de smoke tests para endpoints críticos (auth, usuarios) y servicios (S3, correo) con mocks.

---

## Seguridad (sección existente)

Esta sección conserva y resume la documentación previa del esquema de seguridad basado en JWT y Spring Security con soporte multiempresa.

### Modelo de Datos

#### Empresa
Representa una compañía en el sistema. Cada empresa puede tener múltiples grupos.

| Campo     | Valor de ejemplo         |
|-----------|--------------------------|
| `id`      | `1`                      |
| `nombre`  | `EMPRESA DEMO`           |
| `nit`     | `999`                    |
| `estado`  | `ACTIVO` / `INACTIVO` / `PENDIENTE` |

#### Grupo
Agrupación de usuarios dentro de una empresa.

| Campo     | Valor de ejemplo         |
|-----------|--------------------------|
| `id`      | `1`                      |
| `nombre`  | `GRUPO DEMO`             |
| `empresa` | `1` (referencia a Empresa) |

#### Rol
Define los permisos de un usuario en el sistema.

| Campo     | Valor de ejemplo         |
|-----------|--------------------------|
| `id`      | `1`                      |
| `nombre`  | `ADMIN` / `USER` / `CLIENT` |
| `descripcion` | `Rol administrativo del sistema` |

#### Usuario
Representa un usuario registrado en el sistema. Cada usuario pertenece a un grupo y tiene asignado un rol.

| Campo     | Valor de ejemplo         |
|-----------|--------------------------|
| `id`      | `1`                      |
| `username`| `demo.user`              |
| `password`| `encrypted_password`     |
| `grupo`   | `1` (referencia a Grupo) |
| `rol`     | `1` (referencia a Rol)   |

### Autenticación
- Inicio de sesión en `/auth/login` devuelve un JWT con `username`, `rol`, `empresa`.
- Cada servicio valida JWT y aplica restricciones basadas en rol.

### Roles
- `ADMIN`: Acceso total.
- `USER`: Acceso limitado a sus procesos.
- `CLIENT`: Consulta de estados/documentos.

---

## Licencia
- No se especifica licencia en `pom.xml` ni en el repositorio.
- TODO: definir y añadir una licencia (por ejemplo, MIT, Apache-2.0) o aclarar uso interno.