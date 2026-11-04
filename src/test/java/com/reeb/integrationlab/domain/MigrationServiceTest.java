package com.reeb.integrationlab.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.reeb.integrationlab.api.MigrationRequest;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MigrationServiceTest {
  private final MigrationService service = new MigrationService(new InMemoryRegistry());

  @Test
  void prevents_unreviewed_duplicate_migration() {
    var request = new MigrationRequest("LEGACY-42", "billing-v2", false);
    assertEquals("QUEUED", service.start(request).status());

    assertThrows(DuplicateMigrationException.class, () -> service.start(request));
  }

  @Test
  void allows_an_explicit_audited_retry() {
    service.start(new MigrationRequest("LEGACY-42", "billing-v2", false));

    var response = service.start(new MigrationRequest("LEGACY-42", "billing-v2", true));

    assertEquals("REPLAY_ACCEPTED", response.status());
    assertEquals(true, response.replayed());
  }
}

final class InMemoryRegistry implements MigrationRegistry {
  private final Set<String> references = new HashSet<>();

  @Override
  public boolean registerIfMissing(String legacyReference, String targetSystem) {
    return references.add(legacyReference);
  }
}
