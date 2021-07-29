CREATE TABLE if not exists `b_broadcaster` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brc_code` varchar(10) NOT NULL,
  `brc_name` varchar(100) NOT NULL,
  `brc_contact_mobile` varchar(100) NOT NULL,
  `brc_contact_no` varchar(100) NOT NULL,
  `brc_contact_name` varchar(100) NOT NULL,
  `brc_contact_email` varchar(100) NOT NULL,
  `brc_address` varchar(250) NOT NULL,
  `br_pin` bigint(10) NOT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


insert ignore into m_permission values(null,'organisation','CREATE_BROADCASTER','BROADCASTER','CREATE',0);
insert ignore into m_permission values(null,'organisation','READ_BROADCASTER','BROADCASTER','READ',0);
insert ignore into m_permission values(null,'organisation','UPDATE_BROADCASTER','BROADCASTER','UPDATE',0);
insert ignore into m_permission values(null,'organisation','DELETE_BROADCASTER','BROADCASTER','DELETE',0);



