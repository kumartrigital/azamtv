CREATE TABLE `b_service_availability` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `level` VARCHAR(50) NULL,
  `level_id` BIGINT(20) NULL,
  `service_code` INT(20) NULL,
  PRIMARY KEY (`id`));


ALTER TABLE `b_service_availability` 
ADD COLUMN `createdby_id` BIGINT(20) NULL AFTER `service_code`,
ADD COLUMN `created_date` DATETIME NULL AFTER `createdby_id`,
ADD COLUMN `lastmodified_date` DATETIME NULL AFTER `created_date`,
ADD COLUMN `lastmodifiedby_id` BIGINT(20) NULL AFTER `lastmodified_date`;




INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Service', 'CREATE_SERVICEAVAILBILITY', 'SERVICEAVAILBILITY', 'CREATE', '1');
