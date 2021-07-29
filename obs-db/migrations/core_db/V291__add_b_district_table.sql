CREATE TABLE IF NOT EXISTS `b_district` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `district_code` VARCHAR(20) NOT NULL,
  `district_name` VARCHAR(100) NOT NULL,
  `parent_code` INT(11) NOT NULL,
  `createdby_id` BIGINT(20) NULL DEFAULT NULL,
  `lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
  `created_date` DATETIME NULL DEFAULT NULL,
  `lastmodified_date` DATETIME NULL DEFAULT NULL,
  `is_delete` CHAR(1) NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `state_code_UNIQUE` (`district_code` ASC));
