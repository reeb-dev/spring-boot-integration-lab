package com.reeb.integrationlab.domain;

public interface MigrationRegistry {
  boolean registerIfMissing(String legacyReference, String targetSystem);
}
