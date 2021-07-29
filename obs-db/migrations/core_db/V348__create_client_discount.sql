CREATE TABLE IF NOT EXISTS `b_client_discount` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `level` VARCHAR(45) NOT NULL,
  `discount_type` VARCHAR(45) NOT NULL,
  `discount_value` BIGINT(20) NULL,
  `client_id` BIGINT(20) NOT NULL,
  `is_deleted` CHAR(1) NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  INDEX `foreign_key_client_id_idx` (`client_id` ASC),
  CONSTRAINT `fk_client_disc_m_client_id`
    FOREIGN KEY (`client_id`)
    REFERENCES `m_client` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

INSERT IGNORE INTO m_permission VALUES (null, 'portfolio', 'CREATE_CLIENTDISCOUNT', 'CLIENTDISCOUNT', 'CREATE', '0');
INSERT IGNORE INTO m_permission VALUES (null, 'portfolio', 'UPDATE_CLIENTDISCOUNT', 'CLIENTDISCOUNT', 'UPDATE', '0');
INSERT IGNORE INTO m_permission VALUES (null, 'portfolio', 'DELETE_CLIENTDISCOUNT', 'CLIENTDISCOUNT', 'DELETE', '0');
INSERT IGNORE INTO m_permission VALUES (null, 'portfolio', 'READ_CLIENTDISCOUNT', 'CLIENTDISCOUNT', 'READ', '0');
