package com.reeb.integrationlab.persistence;

import com.reeb.integrationlab.domain.MigrationRegistry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
class JpaMigrationRegistry implements MigrationRegistry {
  private final MigrationRecordRepository repository;

  JpaMigrationRegistry(MigrationRecordRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public boolean registerIfMissing(String legacyReference, String targetSystem) {
    if (repository.existsByLegacyReference(legacyReference)) {
      return false;
    }
    repository.save(new MigrationRecord(legacyReference, targetSystem));
    return true;
  }
}
