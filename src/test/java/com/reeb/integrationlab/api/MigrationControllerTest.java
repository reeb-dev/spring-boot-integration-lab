package com.reeb.integrationlab.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.reeb.integrationlab.domain.MigrationService;
import com.reeb.integrationlab.domain.IdempotencyRegistry;
import com.reeb.integrationlab.domain.StoredMigrationResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class MigrationControllerTest {
  private final MigrationController controller = new MigrationController(
      new MigrationService((reference, target) -> true, new EmptyIdempotencyRegistry()));

  @Test
  void exposes_the_queued_migration_contract() {
    var response = controller.start("migration-42", new MigrationRequest("LEGACY-42", "billing-v2", false));

    assertEquals("LEGACY-42", response.getBody().legacyReference());
    assertEquals("QUEUED", response.getBody().status());
    assertEquals(false, response.getBody().replayed());
    assertEquals("false", response.getHeaders().getFirst("Idempotent-Replay"));
  }
}

final class EmptyIdempotencyRegistry implements IdempotencyRegistry {
  @Override
  public Optional<StoredMigrationResponse> find(String key) {
    return Optional.empty();
  }

  @Override
  public void store(String key, String fingerprint, String legacyReference, String status) {}
}
