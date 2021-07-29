INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'CREATE_TIMEMODEL', 'TIMEMODEL', 'CREATE', '0');

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'CREATE_USAGEPLAN', 'USAGEPLAN', 'CREATE', '0');



CREATE TABLE if not exists `r_timemodel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timemodel_name` varchar(20) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;


CREATE TABLE if not exists `r_usage_rateplan` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `planprice_id` int(11) NOT NULL,
  `timemodel_id` int(11) NOT NULL,
  `rum_id` int(11) NOT NULL,
  `rating_type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


