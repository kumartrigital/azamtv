CREATE TABLE IF NOT EXISTS `b_jv_transaction_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_id` bigint(20) NOT NULL,
  `party_id` varchar(50) NOT NULL,
  `party_type` varchar(50) NOT NULL,
  `type` varchar(50) DEFAULT 'null',
  `account` varchar(50) DEFAULT 'null',
  `amount` DECIMAL(24,4) NOT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `b_jv_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `posting_date` date NOT NULL,
  `notes` varchar(100) DEFAULT 'null',
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
