CREATE TABLE IF NOT EXISTS `b_item_pairing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `service_id` bigint(20) NOT NULL,
  `pairing_date` datetime DEFAULT NULL,
  `unpairing_date` datetime DEFAULT NULL,
  `status` char(1) DEFAULT NULL,
  `serial_no_1` varchar(100) NOT NULL,
  `item_type_1` varchar(100) NOT NULL,
  `serial_no_2` varchar(100) NOT NULL,
  `item_type_2` varchar(100) NOT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
