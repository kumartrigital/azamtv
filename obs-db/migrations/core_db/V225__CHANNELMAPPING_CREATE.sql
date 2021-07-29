CREATE TABLE IF NOT EXISTS `b_prd_ch_mapping` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `service_id` int(20) NOT NULL,
  `channel_id` int(20) NOT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'N',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;


insert ignore into m_permission values(null,'organisation','CREATE_CHANNELMAPPING','CHANNELMAPPING','CREATE',0);
insert ignore into m_permission values(null,'organisation','READ_CHANNELMAPPING','CHANNELMAPPING','READ',0);
insert ignore into m_permission values(null,'organisation','UPDATE_CHANNELMAPPING','CHANNELMAPPING','UPDATE',0);
insert ignore into m_permission values(null,'organisation','DELETE_CHANNELMAPPING','CHANNELMAPPING','DELETE',0);
