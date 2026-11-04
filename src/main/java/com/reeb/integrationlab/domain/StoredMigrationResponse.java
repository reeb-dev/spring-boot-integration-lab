package com.reeb.integrationlab.domain;

public record StoredMigrationResponse(
    String fingerprint,
    String legacyReference,
    String status
) {}
