package com.reeb.integrationlab.api;

import com.reeb.integrationlab.domain.MigrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migrations")
@Tag(name = "Migraciones", description = "Inicio de migraciones legacy con protección idempotente")
class MigrationController {
  private final MigrationService migrationService;

  MigrationController(MigrationService migrationService) {
    this.migrationService = migrationService;
  }

  @PostMapping
  @Operation(
      summary = "Inicia una migración sin duplicar efectos",
      description = "Una repetición con la misma Idempotency-Key y el mismo payload devuelve "
          + "la respuesta persistida. Si el payload cambia, se rechaza con 409.",
      security = @SecurityRequirement(name = "basicAuth"))
  @ApiResponses({
      @ApiResponse(responseCode = "202", description = "Migración aceptada o replay idempotente"),
      @ApiResponse(responseCode = "400", description = "Header o cuerpo inválido"),
      @ApiResponse(responseCode = "401", description = "Autenticación requerida"),
      @ApiResponse(responseCode = "409", description = "Clave reutilizada con otro payload o migración duplicada")
  })
  ResponseEntity<MigrationResponse> start(
      @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey,
      @Valid @RequestBody MigrationRequest request) {
    var result = migrationService.start(idempotencyKey, request);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .header("Idempotent-Replay", Boolean.toString(result.replayed()))
        .body(result.response());
  }
}
