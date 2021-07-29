CREATE TABLE if not exists `b_currency_exchange` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `currency` varchar(20) NOT NULL,
  `status` varchar(10) NOT NULL,
  `base_currency` varchar(20) DEFAULT NULL,
  `conversion_rate` decimal(12,2) DEFAULT NULL,
  `valid_from` date DEFAULT NULL,
  `valid_to` date DEFAULT NULL,
  `is_deleted` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_curr_sts_bcurr` (`status`,`base_currency`,`currency`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;



UPDATE c_configuration SET value='{"billDayOfMonth":"1","billCurrency":"356", "billFrequency":"2"}' WHERE name='bill_profile';

