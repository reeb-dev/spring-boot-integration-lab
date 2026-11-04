package com.reeb.integrationlab.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface MigrationRecordRepository extends JpaRepository<MigrationRecord, Long> {
  boolean existsByLegacyReference(String legacyReference);
}
