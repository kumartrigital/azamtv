CREATE TABLE `b_quote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lead_id` bigint(20) NOT NULL,
  `quote_date` date DEFAULT NULL,
  `quote_status` varchar(20) DEFAULT NULL,
  `total_charge` decimal(22,4) DEFAULT NULL,
  `notes` text,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;



CREATE TABLE `b_quote_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `quote_id` bigint(20) NOT NULL,
  `service_code` varchar(45) DEFAULT NULL,
  `plan_name` varchar(45) DEFAULT NULL,
  `plan_recurirng_charge` decimal(22,4) DEFAULT NULL,
  `plan_onetime_charge` decimal(22,4) DEFAULT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;




INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('CRM', 'CREATE_QUOTES', 'QUOTES', 'CREATE', '0');

