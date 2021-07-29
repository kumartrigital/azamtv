CREATE TABLE IF NOT EXISTS`b_sales_cataloge` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `is_deleted` CHAR(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_key` (`name`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;



insert ignore into m_permission values(null,'organisation','CREATE_SALESCATALOGE','SALESCATALOGE','CREATE',0);
insert ignore into m_permission values(null,'organisation','UPDATE_SALESCATALOGE','SALESCATALOGE','UPDATE',0);
insert ignore into m_permission values(null,'organisation','READ_SALESCATALOGE','SALESCATALOGE','READ',0);
insert ignore into m_permission values(null,'organisation','DELETE_SALESCATALOGE','SALESCATALOGE','DELETE',0);
