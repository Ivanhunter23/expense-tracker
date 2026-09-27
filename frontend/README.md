# Expense Tracker — Angular

Frontend en Angular 22 y TypeScript. Muestra la descripción, el importe y la
categoría de los gastos recibidos mediante `GET /api/expenses`.

## Arrancar

Necesitas Node.js compatible con Angular 22 y npm. Prepara y arranca la API en
el puerto `8081` siguiendo el [README del backend](../README.md).

Desde esta carpeta (`frontend`):

```bash
npm ci
npm start
```

Abre <http://localhost:4200>. El comando `npm start` carga
`src/proxy.conf.json`: el servidor de desarrollo reenvía `/api/**` a
`http://localhost:8081`, conservando la ruta. Esta configuración corresponde
al servidor de desarrollo.

## Verificar

```bash
npm run build
npm test -- --watch=false
```

Las pruebas simulan la respuesta HTTP y comprueban el título y la lista de
gastos. Para estas pruebas no hace falta arrancar Spring ni PostgreSQL.

## Flujo actual

Angular crea `App`, cuyo constructor llama a `loadExpenses()`. La suscripción
de `HttpClient` inicia la petición. Cuando llega el array, `expenses.set(data)`
actualiza la señal; la plantilla lee `expenses()` y `@for` muestra cada gasto.

`expense.ts` describe la forma esperada de los datos para el compilador de
TypeScript.
