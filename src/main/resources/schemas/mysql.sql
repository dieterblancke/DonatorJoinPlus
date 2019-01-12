CREATE TABLE IF NOT EXISTS djp_data (
  id        INT(11) AUTO_INCREMENT   NOT NULL,
  uuid      VARCHAR(36) UNIQUE       NOT NULL,
  toggled   TINYINT(1)               NOT NULL DEFAULT 0,
  slotgroup VARCHAR(128)             NOT NULL DEFAULT 'none',
  PRIMARY KEY (id),
  KEY idx_users (id, uuid)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1;