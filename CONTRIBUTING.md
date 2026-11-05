# Contribuir

Gracias por mejorar este laboratorio. La meta es mantenerlo pequeño, ejecutable y útil para discutir decisiones de integración en Spring Boot.

## Antes de abrir un pull request

1. Abrí un issue para acordar cambios de alcance o de contrato HTTP.
2. Mantené cada PR enfocado en una decisión verificable.
3. Ejecutá `mvn verify`.
4. Actualizá README o `docs/` cuando cambie un comportamiento observable.

## Ideas adecuadas para contribuir

- pruebas de concurrencia para la reserva atómica de claves;
- TTL y limpieza segura de claves de idempotencia;
- ejemplo de outbox para efectos secundarios;
- observabilidad de replays, conflictos y latencia;
- proveedores de persistencia alternativos.

No incluir secretos, credenciales reales ni código de producción de terceros.
