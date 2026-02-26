# Services SaaS (Backend)

Backend monolítico en Java/Spring Boot para una SaaS de **gestión de clientes y órdenes de trabajo** orientada a autónomos y servicios técnicos (mantenimiento, reparaciones, instalaciones).

## Objetivo del proyecto
Construir un backend **profesional, mantenible y monetizable**, aplicando prácticas reales de arquitectura, persistencia, validación, manejo de errores y (próximamente) seguridad con JWT.

---

## Stack
- Java 17
- Spring Boot 3
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- H2 (temporal, entorno local/test)
- OpenAPI/Swagger (si ya lo tienes)
- Testing: JUnit 5 + Mockito (en progreso)

---

## Funcionalidad implementada

### Customers
- CRUD completo
- Validaciones con DTO
- Email único (validación en servicio + constraint en BD)
- 404 si el recurso no existe

### Work Orders
- Crear órdenes de trabajo para un cliente
- Listado paginado con filtros:
    - `status`
    - `customerId` (valida 404 si el customer no existe)
- Orden por defecto: `createdAt DESC`
- Transiciones de estado:
    - `start` → `IN_PROGRESS`
    - `complete` → `COMPLETED`
    - `cancel` → `CANCELLED`
- Manejo de enum inválido → 400
- Optimización de consultas:
    - `@EntityGraph` para evitar N+1 en listados

### Aggregates / Reporting
- Summary agregado por cliente (sin cargar entidades en memoria):
    - `GET /api/customers/{id}/work-orders/summary`
    - Conteo por estado vía `GROUP BY` en BD

---

## API (ejemplos)

### Listado paginado
`GET /api/work-orders?page=0&size=3&sort=createdAt,desc`

### Summary agregado
`GET /api/customers/{id}/work-orders/summary`

---

## Manejo de errores
El backend devuelve errores consistentes usando `ProblemDetail`:
- Errores de validación (400)
- Recurso no encontrado (404)
- Email duplicado (409)
- Estado de work order inválido (400)
- Type mismatch (400)

---

## Seeder (solo dev)
En el profile `dev` se generan datos iniciales:
- 2 customers
- 15 work orders por customer

---

## Ejecutar en local

### Requisitos
- Java 17
- Maven

### Run
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev