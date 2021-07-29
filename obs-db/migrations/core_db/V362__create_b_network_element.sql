CREATE TABLE IF NOT EXISTS `b_network_element` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system_code` varchar(120) NOT NULL,
  `system_name` varchar(45) NOT NULL,
  `status` varchar(45) NOT NULL,
  `is_deleted` char(1) NOT NULL,
  `is_group_supported` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_code_UNIQUE` (`system_code`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

