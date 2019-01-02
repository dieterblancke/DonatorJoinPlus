CREATE TABLE IF NOT EXISTS djp_data (
  id      BIGINT      NOT NULL AUTO_INCREMENT,
  uuid    VARCHAR(36) NOT NULL,
  toggled TINYINT(1)  NOT NULL,
  PRIMARY KEY (id),
  KEY idx_djpdata (id, uuid)
);