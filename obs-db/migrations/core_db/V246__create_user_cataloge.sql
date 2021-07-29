CREATE TABLE IF NOT EXISTS`b_user_cataloge` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `cataloge_id` BIGINT(20) NOT NULL,
  `is_deleted` CHAR(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usrId_catId` (`user_id`, `cataloge_id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;



insert ignore into m_permission values(null,'organisation','CREATE_USERCATALOGE','USERCATALOGE','CREATE',0);
insert ignore into m_permission values(null,'organisation','UPDATE_USERCATALOGE','USERCATALOGE','UPDATE',0);
insert ignore into m_permission values
(null,'organisation','READ_USERCATALOGE','USERCATALOGE','READ',0);
insert ignore into m_permission values(null,'organisation','DELETE_USERCATALOGE','USERCATALOGE','DELETE',0);
insert ignore into m_permission values(null,'organisation','CREATE_CLIENTSIMPLEACTIVATION','CLIENTSIMPLEACTIVATION','CREATE',0);
