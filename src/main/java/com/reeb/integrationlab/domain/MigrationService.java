package com.reeb.integrationlab.domain;

import com.reeb.integrationlab.api.MigrationRequest;
import com.reeb.integrationlab.api.MigrationResponse;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {
  private final MigrationRegistry registry;

  public MigrationService(MigrationRegistry registry) {
    this.registry = registry;
  }

  public MigrationResponse start(MigrationRequest request) {
    boolean firstDelivery = registry.registerIfMissing(request.legacyReference(), request.targetSystem());

    if (!firstDelivery && !request.retry()) {
      throw new DuplicateMigrationException(request.legacyReference());
    }

    return new MigrationResponse(
        request.legacyReference(),
        firstDelivery ? "QUEUED" : "REPLAY_ACCEPTED",
        !firstDelivery
    );
  }
}
