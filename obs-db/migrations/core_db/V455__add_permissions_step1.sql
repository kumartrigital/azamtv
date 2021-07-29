INSERT  IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'CREATE_TEMPLATES', 'TEMPLATES', 'CREATE', '0');


INSERT  IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'UPDATE_TEMPLATES', 'TEMPLATES', 'UPDATE', '0');

CREATE TABLE if not exists`r_input_master` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template_name` varchar(10) DEFAULT NULL,
  `delimited` char(1) DEFAULT NULL,
  `delimiter` varchar(10) DEFAULT NULL,
  `number_of_fields` int(20) DEFAULT NULL,
  `is_header` char(1) DEFAULT NULL,
  `header_record_type` varchar(10) DEFAULT NULL,
  `event_record_type` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=latin1;


CREATE TABLE if not exists`r_input_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_id` int(10) DEFAULT '0',
  `field_name` varchar(45) DEFAULT NULL,
  `field_type` varchar(45) DEFAULT NULL,
  `length` int(10) DEFAULT NULL,
  `identifier_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_r_input_details_1_idx` (`template_id`),
  CONSTRAINT `fk_r_input_details_1` FOREIGN KEY (`template_id`) REFERENCES `r_input_master` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=latin1;








