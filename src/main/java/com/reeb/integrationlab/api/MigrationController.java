package com.reeb.integrationlab.api;

import com.reeb.integrationlab.domain.MigrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migrations")
class MigrationController {
  private final MigrationService migrationService;

  MigrationController(MigrationService migrationService) {
    this.migrationService = migrationService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  MigrationResponse start(@Valid @RequestBody MigrationRequest request) {
    return migrationService.start(request);
  }
}
