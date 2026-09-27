# Expense Tracker

API REST para crear, consultar, actualizar y eliminar gastos en PostgreSQL.
Permite filtrar por categoría y consultar el importe total de todos los gastos.

## Tecnologías

- Java 21 y Maven.
- Spring Boot 4.1.1 y Spring Web.
- Spring Data JPA y Hibernate.
- PostgreSQL y Flyway para las migraciones.
- Bean Validation para validar las peticiones.
- JUnit 5, Mockito, MockMvc y Testcontainers para los tests.

## Requisitos

- JDK 21.
- Maven instalado; los comandos se ejecutan desde la raíz del proyecto.
- PostgreSQL. La guía siguiente utiliza Docker con PostgreSQL 17.
- Docker en ejecución para los tests de integración con Testcontainers.
- `curl` para probar los ejemplos de la API.

## Configuración y arranque

### 1. Configurar las credenciales

Copia el archivo de ejemplo:

```bash
cp .env.example .env
```

Edita `.env` con el usuario y la contraseña de tu PostgreSQL. Para la base de datos
local del siguiente ejemplo, los valores son:

```properties
DB_USERNAME=expense_user
DB_PASSWORD=change_me_local
```

`change_me_local` es una contraseña de ejemplo. Si eliges otra, usa la misma en
`.env` y en `POSTGRES_PASSWORD` del comando siguiente. La aplicación carga `.env`
automáticamente como un archivo de propiedades; `.gitignore` lo excluye de Git.

### 2. Arrancar PostgreSQL

Para crear una base de datos local nueva:

```bash
docker run -d \
  --name expense-tracker-db \
  -p 127.0.0.1:5433:5432 \
  -e POSTGRES_DB=expense_tracker \
  -e POSTGRES_USER=expense_user \
  -e POSTGRES_PASSWORD=change_me_local \
  -v expense-tracker-data:/var/lib/postgresql/data \
  postgres:17
```

El volumen conserva los datos entre arranques. Si el contenedor
`expense-tracker-db` ya existe, arráncalo con:

```bash
docker start expense-tracker-db
```

En ese caso, configura `.env` con sus credenciales existentes.

### 3. Arrancar la API

```bash
mvn spring-boot:run
```

La API escucha en `http://localhost:8081`. La configuración predeterminada conecta
a `jdbc:postgresql://localhost:5433/expense_tracker`.

Al arrancar, Flyway aplica las migraciones de `src/main/resources/db/migration`.
En una base de datos nueva, `V1__create_expenses_table.sql` crea la tabla `expenses`.
Después, Hibernate comprueba que el esquema coincide con la entidad mediante
`spring.jpa.hibernate.ddl-auto=validate`.

Si el puerto `5433` está ocupado, puedes publicar PostgreSQL en `5434` usando
`-p 127.0.0.1:5434:5432` al crear el contenedor. Arranca la API con esa URL:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.datasource.url=jdbc:postgresql://localhost:5434/expense_tracker
```

Para detener la API, usa `Ctrl+C` en la terminal donde la arrancaste.

## Datos de un gasto

Las peticiones `POST` y `PUT` reciben estos campos:

| Campo | Formato | Validación |
| --- | --- | --- |
| `description` | Texto | Obligatorio; no puede estar vacío ni contener solo espacios. |
| `amount` | Número JSON | Obligatorio y mayor que cero. En Java se usa `BigDecimal`. |
| `category` | Texto | Obligatorio; debe coincidir con una categoría del enum. |
| `date` | Texto con formato `YYYY-MM-DD` | Obligatorio. |

Categorías: `FOOD`, `TRANSPORT`, `ENTERTAINMENT`, `HEALTH`, `UTILITIES` y `OTHER`.
Se escriben en mayúsculas.

El cliente envía los cuatro campos en `POST` y `PUT`. PostgreSQL genera el `id`
al crear un gasto; para actualizarlo, el ID se indica en la ruta.

## Endpoints

| Método | Ruta | Resultado |
| --- | --- | --- |
| `GET` | `/api/expenses` | `200`: array de todos los gastos. |
| `GET` | `/api/expenses?category=FOOD` | `200`: array de los gastos de esa categoría. |
| `GET` | `/api/expenses/{id}` | `200`: gasto encontrado; `404`: no existe. |
| `POST` | `/api/expenses` | `201`: gasto creado; `400`: petición inválida. |
| `PUT` | `/api/expenses/{id}` | `200`: gasto actualizado; `404`: no existe; `400`: petición inválida. |
| `DELETE` | `/api/expenses/{id}` | `204`: gasto eliminado, sin body; `404`: no existe. |
| `GET` | `/api/expenses/total` | `200`: número con la suma de todos los importes. |

Una consulta de lista sin resultados devuelve `[]`. El total sin gastos devuelve
`0`. El endpoint de total suma todos los gastos y no recibe un filtro de categoría.

## Ejemplos con curl

`-i` muestra el código HTTP y los headers. `Content-Type: application/json` indica
el formato del body. En estos ejemplos, `--data` hace que `curl` use `POST` cuando
no se especifica otro método.

### Crear un gasto

```bash
curl -sS -i http://localhost:8081/api/expenses \
  -H 'Content-Type: application/json' \
  --data '{"description":"Billete de autobus","amount":3.20,"category":"TRANSPORT","date":"2026-09-26"}'
```

Respuesta: `201 Created`, con el gasto guardado. Ejemplo de body; el ID real puede
ser distinto:

```json
{
  "id": 1,
  "description": "Billete de autobus",
  "amount": 3.20,
  "category": "TRANSPORT",
  "date": "2026-09-26"
}
```

Para los ejemplos siguientes, sustituye `1` por el ID que devolvió tu `POST` y
ejecuta los comandos en la misma terminal:

```bash
EXPENSE_ID=1
```

### Consultar gastos y total

```bash
curl -sS -i http://localhost:8081/api/expenses
curl -sS -i 'http://localhost:8081/api/expenses?category=TRANSPORT'
curl -sS -i "http://localhost:8081/api/expenses/$EXPENSE_ID"
curl -sS -i http://localhost:8081/api/expenses/total
```

### Actualizar el gasto

`PUT` reemplaza los cuatro campos del gasto existente y conserva su ID:

```bash
curl -sS -i -X PUT "http://localhost:8081/api/expenses/$EXPENSE_ID" \
  -H 'Content-Type: application/json' \
  --data '{"description":"Billete de autobus actualizado","amount":4.00,"category":"TRANSPORT","date":"2026-09-26"}'
```

### Eliminar el gasto de ejemplo

Este comando elimina el gasto cuyo ID guardaste en `EXPENSE_ID`:

```bash
curl -sS -i -X DELETE "http://localhost:8081/api/expenses/$EXPENSE_ID"
```

Respuesta: `204 No Content`. Una consulta posterior de ese ID devuelve `404`.

### Comprobar la validación

Una descripción vacía y un importe de cero incumplen las restricciones:

```bash
curl -sS -i http://localhost:8081/api/expenses \
  -H 'Content-Type: application/json' \
  --data '{"description":"","amount":0,"category":"TRANSPORT","date":"2026-09-26"}'
```

Respuesta: `400 Bad Request`. El orden de las claves puede variar:

```json
{
  "errors": {
    "amount": "must be greater than 0",
    "description": "must not be blank"
  }
}
```

`@Valid` rechaza la petición antes de ejecutar el cuerpo del método del controller.
`ApiExceptionHandler` convierte los errores en este JSON; el gasto no se guarda.
Este formato corresponde a errores de validación de campos. Un JSON mal formado
o una categoría desconocida utilizan el manejo de errores predeterminado de Spring.

## Tests

Con Docker en ejecución:

```bash
mvn test
```

| Clase | Qué comprueba |
| --- | --- |
| `ExpenseServiceTest` | Lógica del service con un repository simulado mediante Mockito. |
| `ExpenseControllerTest` | Llamadas directas a métodos del controller con un service simulado. |
| `ExpenseControllerWebTest` | Rutas HTTP, JSON, códigos de respuesta y validación con MockMvc y un service simulado. |
| `ExpenseRepositoryIntegrationTest` | Persistencia JPA, filtro y actualización usando PostgreSQL real en Testcontainers. |

Los tests de integración crean su propio PostgreSQL 16 temporal, con conexión y
credenciales proporcionadas por Testcontainers. Utilizan una base de datos distinta
de la configurada para la API local.

## Recorrido de una petición

```text
HTTP request
→ ExpenseController
→ ExpenseService
→ ExpenseRepository
→ Spring Data JPA / Hibernate
→ PostgreSQL
→ JSON response
```

Spring crea los componentes e inyecta sus dependencias al arrancar. Spring Data
proporciona la implementación de `ExpenseRepository`. Hibernate transforma las
filas de PostgreSQL en objetos `Expense`.

La entrada de la aplicación es `ExpenseTrackerApplication`. Las clases de la etapa
anterior con JDBC manual se retiraron del código actual y se conservan en el
historial de Git.
