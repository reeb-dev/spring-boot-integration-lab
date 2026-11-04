package com.reeb.integrationlab.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.reeb.integrationlab.api.MigrationRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MigrationServiceTest {
  private final MigrationService service = new MigrationService(new InMemoryRegistry(), new InMemoryIdempotencyRegistry());

  @Test
  void prevents_unreviewed_duplicate_migration() {
    var request = new MigrationRequest("LEGACY-42", "billing-v2", false);
    assertEquals("QUEUED", service.start("key-1", request).response().status());

    assertThrows(DuplicateMigrationException.class, () -> service.start("key-2", request));
  }

  @Test
  void allows_an_explicit_audited_retry() {
    service.start("key-1", new MigrationRequest("LEGACY-42", "billing-v2", false));

    var response = service.start("key-2", new MigrationRequest("LEGACY-42", "billing-v2", true)).response();

    assertEquals("REPLAY_ACCEPTED", response.status());
    assertEquals(false, response.replayed());
  }

  @Test
  void replays_a_completed_request_without_executing_business_logic_twice() {
    var request = new MigrationRequest("LEGACY-42", "billing-v2", false);

    var initial = service.start("same-key", request);
    var replay = service.start("same-key", request);

    assertEquals("QUEUED", initial.response().status());
    assertEquals(false, initial.replayed());
    assertEquals("QUEUED", replay.response().status());
    assertEquals(true, replay.replayed());
  }

  @Test
  void rejects_reusing_a_key_for_a_different_payload() {
    service.start("same-key", new MigrationRequest("LEGACY-42", "billing-v2", false));

    assertThrows(
        IdempotencyKeyConflictException.class,
        () -> service.start("same-key", new MigrationRequest("LEGACY-43", "billing-v2", false))
    );
  }
}

final class InMemoryRegistry implements MigrationRegistry {
  private final Set<String> references = new HashSet<>();

  @Override
  public boolean registerIfMissing(String legacyReference, String targetSystem) {
    return references.add(legacyReference);
  }
}

final class InMemoryIdempotencyRegistry implements IdempotencyRegistry {
  private final Map<String, StoredMigrationResponse> responses = new HashMap<>();

  @Override
  public java.util.Optional<StoredMigrationResponse> find(String key) {
    return java.util.Optional.ofNullable(responses.get(key));
  }

  @Override
  public void store(String key, String fingerprint, String legacyReference, String status) {
    responses.put(key, new StoredMigrationResponse(fingerprint, legacyReference, status));
  }
}
