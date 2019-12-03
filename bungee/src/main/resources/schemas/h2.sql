CREATE TABLE IF NOT EXISTS djp_data (
  id        BIGINT       NOT NULL AUTO_INCREMENT,
  uuid      VARCHAR(36)  NOT NULL,
  toggled   TINYINT(1)   NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_djpdata ON `djp_data` (id, uuid);