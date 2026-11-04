package com.reeb.integrationlab.api;

import com.reeb.integrationlab.domain.MigrationService;
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
class MigrationController {
  private final MigrationService migrationService;

  MigrationController(MigrationService migrationService) {
    this.migrationService = migrationService;
  }

  @PostMapping
  ResponseEntity<MigrationResponse> start(
      @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey,
      @Valid @RequestBody MigrationRequest request) {
    var result = migrationService.start(idempotencyKey, request);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .header("Idempotent-Replay", Boolean.toString(result.replayed()))
        .body(result.response());
  }
}
