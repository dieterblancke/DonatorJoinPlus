CREATE TABLE IF NOT EXISTS djp_data (
  id        INT(11) AUTO_INCREMENT   NOT NULL,
  uuid      VARCHAR(36) UNIQUE       NOT NULL,
  toggled   TINYINT(1)               NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_users (id, uuid)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1;