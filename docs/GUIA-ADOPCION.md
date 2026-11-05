# Guía de adopción: idempotencia en Spring Boot

Este documento convierte el ejemplo del laboratorio en una lista de decisiones para una API de producción. No es una receta universal: la semántica de cada operación determina el alcance de la clave, la ventana de reintento y la respuesta guardada.

## 1. Delimitá el efecto de negocio

Una clave de idempotencia no debe ser global. Asociarla a:

- un actor autenticado o tenant;
- una operación concreta, por ejemplo `POST /payments`;
- un payload canónico y su huella;
- una ventana de reintento conocida.

Así se evita que una clave válida para una migración se reutilice contra otra operación o por otra cuenta.

## 2. Elegí qué se persiste

Persistí al menos la clave, la huella del request, el estado y la respuesta que debe devolverse en un replay. Para un `POST` asíncrono puede bastar un identificador de operación y `202 Accepted`; para un pago, suele ser necesario guardar el resultado completo.

## 3. Hacé atómica la primera ejecución

La secuencia "consultar, ejecutar, guardar" es insuficiente bajo concurrencia. Dos solicitudes con la misma clave pueden pasar la consulta al mismo tiempo. En producción, protegé la reserva con una restricción única y una inserción atómica, o con una transacción/locking apropiado para la base elegida.

## 4. Tratá los efectos secundarios

Persistir la respuesta HTTP no evita duplicar un evento, un correo o una llamada a otro proveedor. Para esos casos, combiná el registro de idempotencia con un patrón outbox y consumidores idempotentes.

## 5. Definí errores explícitos

| Escenario | Respuesta sugerida |
| --- | --- |
| clave nueva, operación válida | `202 Accepted` o `201 Created` |
| misma clave, mismo payload | misma respuesta + `Idempotent-Replay: true` |
| misma clave, payload distinto | `409 Conflict` |
| misma entidad, nueva clave y operación no permitida | `409 Conflict` |
| clave expirada | nueva operación o error explícito, según contrato |

## 6. Observá y probá

Medí `idempotency.replay`, `idempotency.conflict`, duración y fallas de reserva. Probá como mínimo: primera solicitud, replay, clave con payload diferente, reglas de negocio duplicadas y concurrencia. Los tests del proyecto cubren los primeros escenarios; la concurrencia queda como evolución intencional.
