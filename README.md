# Spring Boot Integration Lab

Proyecto breve para demostrar criterio de ingeniería al migrar integraciones legacy hacia una API Spring Boot.

## Qué evidencia

- API REST con validación de entrada y respuesta HTTP explícita.
- Regla de negocio de idempotencia: una migración no se repite sin una decisión de reintento auditada.
- Seguridad activada para endpoints de negocio; la autenticación básica es deliberadamente simple en este laboratorio y debe sustituirse por JWT/OAuth2 en un entorno real.
- Idempotencia persistida con Spring Data JPA; H2 local y PostgreSQL mediante Docker Compose.
- Tests unitarios y MVC, más CI en GitHub Actions.

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
  -d '{"legacyReference":"LEGACY-42","targetSystem":"billing-v2","retry":false}'
```

> Para producción, reemplazar Basic Auth por JWT/OAuth2, persistir la idempotencia y publicar eventos con un patrón outbox. El objetivo de este repo es hacer visibles esas decisiones y sus pruebas.

## Pruebas

```bash
mvn verify
```
