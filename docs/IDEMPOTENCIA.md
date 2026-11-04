# Idempotencia práctica en una API Spring Boot

La idempotencia evita que un reintento legítimo del cliente produzca un segundo efecto de negocio.

No equivale a “ignorar duplicados”. La API debe distinguir tres situaciones:

| Caso | Resultado |
| --- | --- |
| Primera solicitud con una clave nueva | Ejecuta la operación y persiste la respuesta. |
| Mismo `Idempotency-Key`, mismo payload | Devuelve la respuesta persistida; no reejecuta el dominio. |
| Mismo `Idempotency-Key`, payload distinto | Devuelve `409 Conflict`: hay una ambigüedad que no debe ocultarse. |

## Contrato HTTP

```http
POST /api/migrations
Idempotency-Key: migration-legacy-42-v1
Content-Type: application/json
```

La primera solicitud responde:

```http
202 Accepted
Idempotent-Replay: false
```

El mismo request reintentado responde:

```http
202 Accepted
Idempotent-Replay: true
```

La misma clave con un cuerpo distinto responde:

```http
409 Conflict
{
  "code": "IDEMPOTENCY_KEY_REUSED_WITH_DIFFERENT_PAYLOAD"
}
```

## Qué hace este laboratorio

1. Calcula una huella determinística del request de migración.
2. Busca una respuesta previa por `Idempotency-Key`.
3. Si existe y la huella coincide, devuelve la respuesta guardada.
4. Si existe y la huella no coincide, rechaza la petición.
5. Si no existe, ejecuta la regla de dominio y persiste la respuesta con JPA.

La idempotencia se mantiene separada de la regla del dominio: otra clave no habilita repetir una migración ya iniciada. Ese límite es esencial para que “reintentar” no se convierta en “duplicar trabajo”.

## Límites deliberados

El laboratorio permite explicar el contrato y sus pruebas, pero una API de producción agrega:

- reserva atómica de la clave ante solicitudes concurrentes;
- TTL o política de retención;
- serialización canónica para fingerprints complejos;
- outbox para eventos y efectos secundarios;
- observabilidad: métricas de replay, conflicto y latencia;
- autenticación/autorización y un scope de clave por actor.
