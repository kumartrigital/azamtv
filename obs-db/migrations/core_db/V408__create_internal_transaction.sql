CREATE TABLE `b_internal_transaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `office_id` BIGINT(20) NOT NULL,
  `client_id` BIGINT(20) NOT NULL,
  `transaction_amount` DECIMAL(24,4) NULL DEFAULT '0.00000',
  `transaction_date` DATETIME NULL DEFAULT NULL,
  `is_deleted` CHAR(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`))ENGINE=InnoDB DEFAULT CHARSET=utf8;

