package com.reeb.integrationlab.persistence;

import com.reeb.integrationlab.domain.IdempotencyRegistry;
import com.reeb.integrationlab.domain.StoredMigrationResponse;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
class JpaIdempotencyRegistry implements IdempotencyRegistry {
  private final IdempotencyRecordRepository repository;

  JpaIdempotencyRegistry(IdempotencyRecordRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<StoredMigrationResponse> find(String key) {
    return repository.findById(key)
        .map(record -> new StoredMigrationResponse(
            record.requestFingerprint(), record.legacyReference(), record.responseStatus()));
  }

  @Override
  @Transactional
  public void store(String key, String fingerprint, String legacyReference, String status) {
    repository.save(new IdempotencyRecord(key, fingerprint, legacyReference, status));
  }
}
