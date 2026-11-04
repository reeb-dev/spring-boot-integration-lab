package com.reeb.integrationlab.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.reeb.integrationlab.domain.MigrationService;
import org.junit.jupiter.api.Test;

class MigrationControllerTest {
  private final MigrationController controller = new MigrationController(new MigrationService((reference, target) -> true));

  @Test
  void exposes_the_queued_migration_contract() {
    var response = controller.start(new MigrationRequest("LEGACY-42", "billing-v2", false));

    assertEquals("LEGACY-42", response.legacyReference());
    assertEquals("QUEUED", response.status());
    assertEquals(false, response.replayed());
  }
}
