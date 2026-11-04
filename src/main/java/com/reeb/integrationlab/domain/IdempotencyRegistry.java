package com.reeb.integrationlab.domain;

import java.util.Optional;

public interface IdempotencyRegistry {
  Optional<StoredMigrationResponse> find(String key);

  void store(String key, String fingerprint, String legacyReference, String status);
}
