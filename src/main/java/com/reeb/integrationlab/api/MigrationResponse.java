package com.reeb.integrationlab.api;

public record MigrationResponse(String legacyReference, String status, boolean replayed) {}
