CREATE TABLE if not exists `b_usagerate_balance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rateplan_id` bigint(20) DEFAULT NULL,
  `tier_id` bigint(20) DEFAULT NULL,
  `rum` bigint(20) DEFAULT NULL,
  `timeperiod_id` bigint(20) DEFAULT NULL,
  `gl_id` bigint(20) DEFAULT NULL,
  `uom` bigint(20) DEFAULT NULL,
  `unit` bigint(20) DEFAULT NULL,
  `rate` decimal(19,6) DEFAULT NULL,
  `currency_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE if not exists `r_usagerate_qty_tier` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tier_name` VARCHAR(45) NULL DEFAULT NULL,
  `usagerateplan_id` BIGINT(20) NULL DEFAULT NULL,
  `start_range` BIGINT(20) NULL DEFAULT NULL,
  `end_range` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'CREATE_USAGEBALANCE', 'USAGEBALANCE', 'CREATE', '0');


INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'CREATE_USAGERATEQUANTITYTIER', 'USAGERATEQUANTITYTIER', 'CREATE', '0');





