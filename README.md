# 🔐 Esquema de Seguridad con JWT y Spring Security

Este proyecto implementa un sistema de autenticación y autorización utilizando **JWT** (JSON Web Token) y **Spring Security**, con soporte para múltiples empresas, grupos de usuarios y roles.

---

## 🧱 Modelo de Datos

El modelo de seguridad está compuesto por las siguientes entidades clave:

### 🏢 Empresa

Representa una compañía en el sistema. Cada empresa puede tener múltiples grupos.

| Campo     | Valor de ejemplo         |
|-----------|--------------------------|
| `id`      | `1`                      |
| `nombre`  | `EMPRESA DEMO`           |
| `nit`     | `999`                    |
| `estado`  | `ACTIVO` / `INACTIVO` / `PENDIENTE` |

---

### 👥 Grupo

Agrupación de usuarios dentro de una empresa.

| Campo     | Valor de ejemplo         |
|-----------|--------------------------|
| `id`      | `1`                      |
| `nombre`  | `GRUPO DEMO`             |
| `empresa` | `1` (referencia a Empresa) |

---

### 🛡 Rol

Define los permisos de un usuario en el sistema.

| Campo     | Valor de ejemplo         |
|-----------|--------------------------|
| `id`      | `1`                      |
| `nombre`  | `ADMIN` / `USER` / `CLIENT` |
| `descripcion` | `Rol administrativo del sistema` |

---

### 👤 Usuario

Representa un usuario registrado en el sistema. Cada usuario pertenece a un grupo y tiene asignado un rol.

| Campo     | Valor de ejemplo         |
|-----------|--------------------------|
| `id`      | `1`                      |
| `username`| `demo.user`              |
| `password`| `encrypted_password`     |
| `grupo`   | `1` (referencia a Grupo) |
| `rol`     | `1` (referencia a Rol)   |

---

## 🔐 Seguridad

- Los usuarios se autentican mediante el endpoint `/auth/login`.
- El sistema genera un token JWT que incluye:
    - `username`
    - `rol`
    - `empresa`
- Cada microservicio puede validar el JWT y aplicar restricciones según el rol.

---

## 📌 Roles permitidos

- `ADMIN`: Acceso total a todas las funciones y configuraciones.
- `USER`: Acceso limitado para gestionar sus procesos y tareas.
- `CLIENT`: Solo puede consultar estados y documentos específicos.

---

## 🧪 Datos de ejemplo

```json
{
  "empresa": {
    "id": 1,
    "nombre": "EMPRESA DEMO",
    "nit": "999",
    "estado": "ACTIVO"
  },
  "grupo": {
    "id": 1,
    "nombre": "GRUPO DEMO",
    "empresa": 1
  },
  "rol": {
    "id": 1,
    "nombre": "ADMIN",
    "descripcion": "Rol administrativo del sistema"
  },
  "usuario": {
    "id": 1,
    "username": "demo.user",
    "password": "********",
    "grupo": 1,
    "rol": 1
  }
}