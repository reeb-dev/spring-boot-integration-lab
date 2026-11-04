package com.reeb.integrationlab.domain;

public class IdempotencyKeyConflictException extends RuntimeException {
  public IdempotencyKeyConflictException(String key) {
    super("The Idempotency-Key was already used with a different request: " + key);
  }
}
