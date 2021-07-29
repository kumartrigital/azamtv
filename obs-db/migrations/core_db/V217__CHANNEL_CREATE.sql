 CREATE TABLE IF NOT EXISTS `b_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `channel_name` varchar(100) NOT NULL,
  `channel_category` varchar(10) NOT NULL,
  `channel_type` varchar(10) NOT NULL,
  `is_local_channel` char(1) DEFAULT 'N',
  `is_hd_channel` char(2) DEFAULT 'N',
  `channel_sequence` bigint(10) NOT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


insert ignore into m_permission values(null,'organisation','CREATE_CHANNEL','CHANNEL','CREATE',0);
insert ignore into m_permission values(null,'organisation','UPDATE_CHANNEL','CHANNEL','UPDATE',0);
insert ignore into m_permission values(null,'organisation','READ_CHANNEL','CHANNEL','READ',0);
insert ignore into m_permission values(null,'organisation','DELETE_CHANNEL','CHANNEL','DELETE',0);
