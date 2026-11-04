package com.reeb.integrationlab.domain;

public class DuplicateMigrationException extends RuntimeException {
  public DuplicateMigrationException(String legacyReference) {
    super("Migration " + legacyReference + " was already accepted. Use retry=true only after an audited retry decision.");
  }
}
