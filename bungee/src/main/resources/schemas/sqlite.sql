CREATE TABLE IF NOT EXISTS djp_data (
  id         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  uuid       VARCHAR(36)      NOT NULL,
  toggled    TINYINT(1)       NOT NULL DEFAULT 0,
  slotgroup  VARCHAR(128)     NOT NULL DEFAULT 'none',
  joinsound  VARCHAR(128)     DEFAULT NULL,
  leavesound VARCHAR(128)     DEFAULT NULL
);

CREATE INDEX IF NOT EXISTS idx_djpdata ON `djp_data` (id, uuid);