# Pruebas locales de Mercado Pago (srvapidataregister)

Este documento explica cómo probar los endpoints actuales de pagos por suscripción vía Mercado Pago usando Postman o cURL.

Fecha: 2025-10-20

---

## 1) Prerrequisitos
- Java 17 instalado.
- PostgreSQL en ejecución (o ajusta `application-local.properties` a tu entorno).
- Perfil `local` activo (ya viene por defecto): `spring.profiles.active=local`.
- Credenciales sandbox de Mercado Pago en `src/main/resources/application-local.properties`:
  - `mp.access-token=TEST-...`
  - `mp.public-key=TEST-...`
  - `mp.currency=COP` (recomendado para empezar)
- Puerto local: 8081 (configurado en `application-local.properties`).

## 2) Iniciar la aplicación
En Windows:

mvnw.cmd spring-boot:run

Verifica que arranque sin errores y escuche en: http://localhost:8081

---

## 3) Postman: colección lista para importar (copiar y pegar)
Copia el siguiente JSON en un archivo `.json` (por ejemplo `srvapidataregister-mercadopago.postman_collection.json`) y luego impórtalo en Postman (File -> Import -> Raw text o seleccionar archivo):

{
  "info": {
    "name": "srvapidataregister - Mercado Pago (local)",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Listar planes (GET /pay/plans)",
      "request": {
        "method": "GET",
        "header": [
          { "key": "Accept", "value": "application/json" }
        ],
        "url": {
          "raw": "{{baseUrl}}/pay/plans"
        }
      }
    },
    {
      "name": "Suscribirse mensual (POST /pay/subscribe)",
      "request": {
        "method": "POST",
        "header": [
          { "key": "Content-Type", "value": "application/json" },
          { "key": "Accept", "value": "application/json" }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"planId\": \"BASIC\",\n  \"period\": \"MONTHLY\",\n  \"payerEmail\": \"comprador@test.com\",\n  \"empresaId\": 1\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/pay/subscribe"
        },
        "description": "Crea una suscripción mensual. Si empresaId resuelve a CO, la moneda será COP."
      }
    },
    {
      "name": "Suscribirse anual (POST /pay/subscribe)",
      "request": {
        "method": "POST",
        "header": [
          { "key": "Content-Type", "value": "application/json" },
          { "key": "Accept", "value": "application/json" }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"planId\": \"PRO\",\n  \"period\": \"ANNUAL\",\n  \"payerEmail\": \"comprador@test.com\",\n  \"empresaId\": 1\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/pay/subscribe"
        },
        "description": "Crea una suscripción anual con 20% de descuento (12 * mensual * 0.8)."
      }
    },
    {
      "name": "Simular webhook (POST /pay/webhook)",
      "request": {
        "method": "POST",
        "header": [
          { "key": "Content-Type", "value": "application/json" }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"type\": \"payment\",\n  \"action\": \"payment.created\",\n  \"data\": { \"id\": \"1234567890\" },\n  \"date_created\": \"2025-01-01T00:00:00Z\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/pay/webhook"
        },
        "description": "Simula la notificación de MP a tu endpoint local."
      }
    }
  ],
  "variable": [
    { "key": "baseUrl", "value": "http://localhost:8081" }
  ]
}

Notas:
- La variable `baseUrl` viene dentro de la colección y apunta a `http://localhost:8081`.
- Si tuvieras seguridad activada y ves 401, agrega tu header Authorization: `Bearer <tu_jwt>` en Postman.

---

## 4) Ejemplos con cURL
- Listar planes:

curl -X GET "http://localhost:8081/pay/plans" -H "Accept: application/json"

- Suscripción mensual BASIC:

curl -X POST "http://localhost:8081/pay/subscribe" \
  -H "Content-Type: application/json" -H "Accept: application/json" \
  -d '{
    "planId": "BASIC",
    "period": "MONTHLY",
    "payerEmail": "comprador@test.com",
    "empresaId": 1
  }'

- Suscripción anual PRO:

curl -X POST "http://localhost:8081/pay/subscribe" \
  -H "Content-Type: application/json" -H "Accept: application/json" \
  -d '{
    "planId": "PRO",
    "period": "ANNUAL",
    "payerEmail": "comprador@test.com",
    "empresaId": 1
  }'

- Simular webhook (para probar el endpoint):

curl -X POST "http://localhost:8081/pay/webhook" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "payment",
    "action": "payment.created",
    "data": { "id": "1234567890" },
    "date_created": "2025-01-01T00:00:00Z"
  }'

---

## 5) Respuestas esperadas y tips
- GET /pay/plans: devuelve arreglo de planes con `monthlyPrice` y `annualPrice` (en COP si `mp.currency=COP`).
- POST /pay/subscribe:
  - Plan FREE: `nextAction = none`, `message = "Plan gratuito, no requiere pago."`.
  - Plan de pago: `nextAction = redirect` y `redirectUrl` (si el cliente de MP está cableado al SDK y las credenciales son válidas). Si aún está en modo placeholder, verás una URL de ejemplo.
- Error `PRICE_UNAVAILABLE_FOR_CURRENCY`: asegura que la moneda sea `COP` (via `mp.currency=COP` o que `empresaId` apunte a país CO) para usar los precios definidos.
- 401 Unauthorized: si tu seguridad exige JWT, agrega `Authorization: Bearer <token>`.

---

## 6) Webhook y URLs públicas (sandbox)
Para recibir notificaciones reales de Mercado Pago en local, usa un túnel (ngrok/Cloudflared) y configura las URLs en `application-local.properties`:

mp.successUrl=https://<tu-dominio-publico>/pay/return/success
mp.pendingUrl=https://<tu-dominio-publico>/pay/return/pending
mp.failureUrl=https://<tu-dominio-publico>/pay/return/failure
mp.webhookUrl=https://<tu-dominio-publico>/pay/webhook

Luego reinicia la app y actualiza la preferencia/preapproval.

---