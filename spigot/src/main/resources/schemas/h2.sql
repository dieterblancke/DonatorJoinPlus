CREATE TABLE IF NOT EXISTS djp_data (
  id                 BIGINT          NOT NULL AUTO_INCREMENT,
  uuid               VARCHAR(36)     NOT NULL,
  toggled            TINYINT(1)      NOT NULL DEFAULT 0,
  slotgroup          VARCHAR(128)    NOT NULL DEFAULT 'none',
  joinsound          VARCHAR(128)    DEFAULT NULL,
  join_volume        INT             DEFAULT 20,
  join_pitch         INT             DEFAULT -20,
  leavesound         VARCHAR(128)    DEFAULT NULL,
  leave_volume       INT             DEFAULT 20,
  leave_pitch        INT             DEFAULT -20,
  soundtoggled       TINYINT(1)      NOT NULL DEFAULT 0,
  fireworktoggled    TINYINT(1)      NOT NULL DEFAULT 0,
  messagesmuted      TINYINT(1)      NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_djpdata ON `djp_data` (id, uuid);