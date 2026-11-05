# Spring Boot Integration Lab

[![CI](https://github.com/reeb-dev/spring-boot-integration-lab/actions/workflows/ci.yml/badge.svg)](https://github.com/reeb-dev/spring-boot-integration-lab/actions/workflows/ci.yml)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?logo=springboot&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

Proyecto ejecutable para demostrar criterio de ingeniería al migrar integraciones legacy hacia una API Spring Boot. Modela un caso frecuente: el cliente reintenta una operación tras un timeout y el backend debe evitar crear dos efectos de negocio.

![Flujo de una operación idempotente](idempotencia-spring-boot-linkedin.png)

## El problema en 30 segundos

```mermaid
sequenceDiagram
    participant Client as Cliente
    participant API as API Spring Boot
    participant DB as JPA / PostgreSQL
    Client->>API: POST /migrations + Idempotency-Key
    API->>DB: Reserva/consulta la clave
    alt Clave nueva
        API->>DB: Persiste resultado aceptado
        API-->>Client: 202 + Idempotent-Replay: false
    else Misma clave, mismo payload
        DB-->>API: Respuesta persistida
        API-->>Client: 202 + Idempotent-Replay: true
    else Misma clave, payload distinto
        API-->>Client: 409 Conflict
    end
```

## Qué demuestra

- API REST con validación de entrada y respuesta HTTP explícita.
- Idempotencia HTTP real: `Idempotency-Key` persiste la primera respuesta. Un reintento con la misma clave y el mismo payload responde sin ejecutar otra vez la regla de negocio.
- Seguridad activada para endpoints de negocio; la autenticación básica es deliberadamente simple en este laboratorio y debe sustituirse por JWT/OAuth2 en un entorno real.
- Idempotencia persistida con Spring Data JPA; H2 local y PostgreSQL mediante Docker Compose.
- Tests unitarios y MVC, más CI en GitHub Actions.

## Probar la API desde el navegador

Con la aplicación levantada, abrí [Swagger UI](http://localhost:8080/swagger-ui/index.html). El contrato OpenAPI también está disponible en [`/v3/api-docs`](http://localhost:8080/v3/api-docs).

En Swagger, seleccioná **Authorize** e ingresá `demo` / `demo` para el HTTP Basic del laboratorio. La documentación es pública; el endpoint de negocio conserva autenticación.

## Ejecutar

```bash
mvn spring-boot:run
```

Para ejecutar con PostgreSQL:

```bash
docker compose up -d
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

```bash
curl -u demo:demo -X POST http://localhost:8080/api/migrations \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: migration-legacy-42-v1' \
  -d '{"legacyReference":"LEGACY-42","targetSystem":"billing-v2","retry":false}'
```

Repetir exactamente la misma petición devuelve `202 Accepted`, el mismo resultado y el header `Idempotent-Replay: true`. Reutilizar la clave con otro payload devuelve `409 Conflict`; utilizar otra clave para una migración ya iniciada también se rechaza, salvo que exista un reintento explícito y auditado.

### Colección Bruno

La carpeta [`bruno/`](bruno/) contiene las tres peticiones listas para importar en [Bruno](https://www.usebruno.com/): crear una migración, repetirla y comprobar el conflicto por payload alterado. Ejecutalas en ese orden para ver el contrato idempotente completo.

## Qué protege la idempotencia

1. El cliente puede reintentar por timeout sin crear dos migraciones.
2. La misma clave no puede ocultar un payload distinto: se compara una huella del request.
3. El resultado aceptado queda persistido con JPA, incluso si el proceso se reinicia.
4. La clave de idempotencia no reemplaza las reglas del dominio: una nueva clave no habilita duplicar una migración.

## Decisiones que importan en producción

- La clave se debe limitar a una operación y a un actor: no debe ser una clave global reutilizable.
- La huella debe incluir el payload canónico; si el mismo key llega con datos distintos, la API debe responder `409 Conflict`.
- La reserva de la clave debe ser atómica para que dos solicitudes concurrentes no ejecuten el efecto de negocio dos veces.
- Conviene definir una retención/TTL para limpiar claves antiguas sin perder la ventana de reintento esperada.
- Para efectos secundarios (eventos, correos, pagos), la idempotencia HTTP se complementa con outbox y consumidores idempotentes.

> Para producción, reemplazar Basic Auth por JWT/OAuth2, poner un límite de retención para las claves, garantizar reserva atómica ante concurrencia y publicar eventos con un patrón outbox. El objetivo de este repo es hacer visibles esas decisiones y sus pruebas.

## Pruebas

```bash
mvn verify
```

## Llevá este patrón a producción

Este laboratorio mantiene el ejemplo deliberadamente acotado. Antes de adoptarlo en un sistema real, revisá [Idempotencia práctica](docs/IDEMPOTENCIA.md) y la [guía de adopción](docs/GUIA-ADOPCION.md), y completá la lista de producción:

- asignar la clave a una operación y un actor autenticado;
- reservar la clave de forma atómica ante solicitudes concurrentes;
- definir TTL, limpieza y ventana de reintento;
- emitir efectos secundarios con outbox y consumidores idempotentes;
- medir replays, conflictos, latencia y claves expiradas.

## Contribuir

Las ideas y criterios para cambios están en [CONTRIBUTING.md](CONTRIBUTING.md). El historial de versiones se mantiene en [CHANGELOG.md](CHANGELOG.md).
