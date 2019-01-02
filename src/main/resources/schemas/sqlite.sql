CREATE TABLE IF NOT EXISTS djp_data (
  id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  uuid    VARCHAR(36) NOT NULL,
  toggled TINYINT(1)  NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_djpdata ON `djp_data` (id, uuid);