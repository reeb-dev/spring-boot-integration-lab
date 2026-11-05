# Changelog

## 1.0.0 - 2026-11-04

- API REST de migraciones con `Idempotency-Key` persistida mediante JPA.
- Validación de replay, conflicto de payload y reglas de migración duplicada.
- Seguridad HTTP Basic solo para el entorno de laboratorio.
- Ejecución local con H2 y perfil PostgreSQL mediante Docker Compose.
- Tests unitarios/MVC y CI en GitHub Actions.
- Contrato OpenAPI/Swagger UI y colección Bruno para probar los tres flujos.
