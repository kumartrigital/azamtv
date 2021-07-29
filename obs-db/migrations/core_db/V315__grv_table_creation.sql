CREATE TABLE if not exists `b_grv` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `requested_date` DATETIME NOT NULL,
  `item_master_id` INT(20) NOT NULL,
  `from_office` BIGINT(20) NOT NULL,
  `to_office` BIGINT(20) NOT NULL,
  `orderd_quantity` BIGINT(20) NOT NULL,
  `received_quantity` BIGINT(20) NOT NULL DEFAULT '0',
  `status` VARCHAR(20) NOT NULL DEFAULT 'New',
  `createdby_id` BIGINT(20) NULL DEFAULT NULL,
  `created_date` DATETIME NULL DEFAULT NULL,
  `lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
  `lastmodified_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`));


insert ignore into m_permission values(null,'logistics','CREATE_GRV','GRV','CREATE',0);
insert ignore into m_permission values(null,'logistics','MOVE_GRV','GRV','MOVE',0);
