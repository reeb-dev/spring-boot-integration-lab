# Borrador de publicación para LinkedIn

## Texto

Un timeout no siempre significa que una operación falló.

En una API, el cliente puede perder la respuesta y reintentar una misma solicitud. Si el backend no está preparado, ese segundo intento puede crear otra migración, otro cobro o un segundo evento.

La idempotencia convierte ese reintento en un caso explícito del contrato.

En este laboratorio con Spring Boot implementé `Idempotency-Key` para una operación de migración:

• Primera solicitud: procesa la operación y persiste la respuesta.

• Mismo key + mismo payload: devuelve la respuesta almacenada con `Idempotent-Replay: true`; la regla de negocio no se ejecuta otra vez.

• Mismo key + payload distinto: responde `409 Conflict`. Reutilizar una clave no puede esconder una solicitud diferente.

Lo importante: la idempotencia HTTP no reemplaza las reglas del dominio. Usar otra clave no habilita duplicar una migración que ya existe.

El ejemplo incluye Spring Boot, JPA, H2/PostgreSQL, Docker, tests y CI. También documenta qué falta para producción: reserva atómica frente a concurrencia, TTL de claves, outbox para efectos secundarios, métricas y seguridad por actor.

Repositorio: https://github.com/reeb-dev/spring-boot-integration-lab

¿En qué operación de tu sistema un reintento hoy podría duplicar un efecto de negocio?

#Java #SpringBoot #APIs #Backend #ArquitecturaDeSoftware #Idempotencia #Testing #Docker

## Imagen

`../idempotencia-spring-boot-linkedin.png`

La composición muestra el mismo request reintentado, una sola ejecución, respuesta persistida y el conflicto cuando cambia el payload con la misma clave.
