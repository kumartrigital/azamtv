CREATE TABLE IF NOT EXISTS `b_provision_code_mapping` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `provision_code` varchar(25) DEFAULT NULL,
  `network_code` varchar(25) DEFAULT NULL,
  `provision_value` varchar(25) NOT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert ignore into m_permission values(null,'portfolio','CREATE_PROVISIONCODEMAPPING','PROVISIONCODEMAPPING','CREATE',0);
insert ignore into m_permission values(null,'portfolio','UPDATE_PROVISIONCODEMAPPING','PROVISIONCODEMAPPING','UPDATE',0);
insert ignore into m_permission values(null,'portfolio','READ_PROVISIONCODEMAPPING','PROVISIONCODEMAPPING','READ',0);
insert ignore into m_permission values(null,'portfolio','DELETE_PROVISIONCODEMAPPING','PROVISIONCODEMAPPING','DELETE',0);
