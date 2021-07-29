CREATE TABLE IF NOT EXISTS `b_sales_cataloge_mapping` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `cataloge_id` BIGINT(20) NOT NULL,
  `plan_id` BIGINT(20) NOT NULL,
  `is_deleted` CHAR(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert ignore into m_permission values(null,'organisation','CREATE_SALESCATALOGEMAPPING','SALESCATALOGEMAPPING','CREATE',0);
insert ignore into m_permission values(null,'organisation','UPDATE_SALESCATALOGEMAPPING','SALESCATALOGEMAPPING','UPDATE',0);
insert ignore into m_permission values(null,'organisation','READ_SALESCATALOGEMAPPING','SALESCATALOGEMAPPING','READ',0);
insert ignore into m_permission values(null,'organisation','DELETE_SALESCATALOGEMAPPING','SALESCATALOGEMAPPING','DELETE',0);


