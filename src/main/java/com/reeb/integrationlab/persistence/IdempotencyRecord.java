package com.reeb.integrationlab.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
class IdempotencyRecord {
  @Id
  @Column(nullable = false, updatable = false, length = 120)
  private String idempotencyKey;

  @Column(nullable = false, updatable = false, length = 512)
  private String requestFingerprint;

  @Column(nullable = false, updatable = false)
  private String legacyReference;

  @Column(nullable = false, updatable = false)
  private String responseStatus;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  protected IdempotencyRecord() {}

  IdempotencyRecord(String idempotencyKey, String requestFingerprint, String legacyReference, String responseStatus) {
    this.idempotencyKey = idempotencyKey;
    this.requestFingerprint = requestFingerprint;
    this.legacyReference = legacyReference;
    this.responseStatus = responseStatus;
    this.createdAt = Instant.now();
  }

  String requestFingerprint() { return requestFingerprint; }
  String legacyReference() { return legacyReference; }
  String responseStatus() { return responseStatus; }
}
