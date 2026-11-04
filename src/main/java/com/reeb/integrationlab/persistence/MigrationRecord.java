package com.reeb.integrationlab.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
class MigrationRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private String legacyReference;

  @Column(nullable = false)
  private String targetSystem;

  @Column(nullable = false, updatable = false)
  private Instant queuedAt;

  protected MigrationRecord() {}

  MigrationRecord(String legacyReference, String targetSystem) {
    this.legacyReference = legacyReference;
    this.targetSystem = targetSystem;
    this.queuedAt = Instant.now();
  }
}
