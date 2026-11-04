package com.reeb.integrationlab.domain;

import com.reeb.integrationlab.api.MigrationRequest;
import com.reeb.integrationlab.api.MigrationResponse;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {
  private final MigrationRegistry registry;
  private final IdempotencyRegistry idempotencyRegistry;

  public MigrationService(MigrationRegistry registry, IdempotencyRegistry idempotencyRegistry) {
    this.registry = registry;
    this.idempotencyRegistry = idempotencyRegistry;
  }

  public IdempotentMigration start(String idempotencyKey, MigrationRequest request) {
    String fingerprint = fingerprint(request);
    var storedResponse = idempotencyRegistry.find(idempotencyKey);

    if (storedResponse.isPresent()) {
      var previous = storedResponse.get();
      if (!previous.fingerprint().equals(fingerprint)) {
        throw new IdempotencyKeyConflictException(idempotencyKey);
      }
      return new IdempotentMigration(
          new MigrationResponse(previous.legacyReference(), previous.status(), true),
          true
      );
    }

    boolean firstDelivery = registry.registerIfMissing(request.legacyReference(), request.targetSystem());

    if (!firstDelivery && !request.retry()) {
      throw new DuplicateMigrationException(request.legacyReference());
    }

    var response = new MigrationResponse(
        request.legacyReference(),
        firstDelivery ? "QUEUED" : "REPLAY_ACCEPTED",
        false
    );
    idempotencyRegistry.store(idempotencyKey, fingerprint, response.legacyReference(), response.status());
    return new IdempotentMigration(response, false);
  }

  private String fingerprint(MigrationRequest request) {
    return String.join("|", request.legacyReference(), request.targetSystem(), Boolean.toString(request.retry()));
  }
}
