package com.reeb.integrationlab.api;

import com.reeb.integrationlab.domain.DuplicateMigrationException;
import com.reeb.integrationlab.domain.IdempotencyKeyConflictException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {
  @ExceptionHandler(DuplicateMigrationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  Map<String, String> duplicate(DuplicateMigrationException exception) {
    return Map.of("code", "DUPLICATE_MIGRATION", "message", exception.getMessage());
  }

  @ExceptionHandler(IdempotencyKeyConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  Map<String, String> idempotencyKeyConflict(IdempotencyKeyConflictException exception) {
    return Map.of("code", "IDEMPOTENCY_KEY_REUSED_WITH_DIFFERENT_PAYLOAD", "message", exception.getMessage());
  }
}
