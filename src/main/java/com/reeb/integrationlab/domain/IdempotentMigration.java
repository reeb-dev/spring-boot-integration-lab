package com.reeb.integrationlab.domain;

import com.reeb.integrationlab.api.MigrationResponse;

public record IdempotentMigration(MigrationResponse response, boolean replayed) {}
