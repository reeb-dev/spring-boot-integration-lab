package com.reeb.integrationlab.api;

import jakarta.validation.constraints.NotBlank;

public record MigrationRequest(
    @NotBlank String legacyReference,
    @NotBlank String targetSystem,
    boolean retry
) {}
