CREATE TABLE IF NOT EXISTS djp_data (
  id                 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  uuid               VARCHAR(36)      NOT NULL,
  toggled            TINYINT(1)       NOT NULL DEFAULT 0,
  slotgroup          VARCHAR(128)     NOT NULL DEFAULT 'none',
  joinsound          VARCHAR(128)     DEFAULT NULL,
  join_volume        INTEGER          DEFAULT 20,
  join_pitch         INTEGER          DEFAULT -20,
  leavesound         VARCHAR(128)     DEFAULT NULL,
  leave_volume       INTEGER          DEFAULT 20,
  leave_pitch        INTEGER          DEFAULT -20,
  soundtoggled       TINYINT(1)       NOT NULL DEFAULT 0,
  fireworktoggled    TINYINT(1)       NOT NULL DEFAULT 0,
  messagesmuted      TINYINT(1)       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_djpdata ON `djp_data` (id, uuid);