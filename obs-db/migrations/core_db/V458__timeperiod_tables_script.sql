CREATE TABLE if not exists `r_time_period` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timeperiod_name` varchar(20) NOT NULL,
  `timemodel_id` int(11) NOT NULL,
  `startyear` int(5) DEFAULT '0',
  `endyear` int(5) DEFAULT '0',
  `startmonth` int(2) DEFAULT '0',
  `endmonth` int(2) DEFAULT '0',
  `startday` int(2) DEFAULT '0',
  `endday` int(2) DEFAULT '0',
  `starttime` time DEFAULT '00:00:00',
  `endtime` time DEFAULT '00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_t_timemodel_idx1_idx` (`timemodel_id`),
  CONSTRAINT `fk_t_timemodel_idx1` FOREIGN KEY (`timemodel_id`) REFERENCES `r_time_period` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'CREATE_TIMEPERIOD', 'TIMEPERIOD', 'CREATE', '0');






