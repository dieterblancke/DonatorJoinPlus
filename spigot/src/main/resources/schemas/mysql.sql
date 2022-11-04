CREATE TABLE IF NOT EXISTS djp_data (
  id                 INT(11) AUTO_INCREMENT   NOT NULL,
  uuid               VARCHAR(36) UNIQUE       NOT NULL,
  toggled            TINYINT(1)               NOT NULL DEFAULT 0,
  slotgroup          VARCHAR(128)             NOT NULL DEFAULT 'none',
  joinsound          VARCHAR(128)             DEFAULT NULL,
  join_volume        INT(11)                  DEFAULT 20,
  join_pitch         INT(11)                  DEFAULT -20,
  leavesound         VARCHAR(128)             DEFAULT NULL,
  leave_volume       INT(11)                  DEFAULT 20,
  leave_pitch        INT(11)                  DEFAULT -20,
  soundtoggled       TINYINT(1)               NOT NULL DEFAULT 0,
  fireworktoggled    TINYINT(1)               NOT NULL DEFAULT 0,
  messagesmuted      TINYINT(1)               NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_users (id, uuid)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1;