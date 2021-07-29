CREATE TABLE if not exists `b_office_ticket` (
  `id` BIGINT(20) NOT NULL ,
  `office_id` BIGINT(20) NULL,
  `priority` VARCHAR(20) NULL,
  `problem_code` BIGINT(20) NULL,
  `description` TEXT NULL,
  `status` VARCHAR(20) NULL,
  `status_code` VARCHAR(20) NULL,
  `resolution_description` VARCHAR(100) NULL,
  `sub_category` VARCHAR(100) NULL,
  `assigned_to` BIGINT(20) NULL,
  `source` VARCHAR(50) NULL,
  `closed_date` DATETIME NULL,
  `createdby_id` BIGINT(20) NULL,
  `created_date` DATETIME NULL,
  `source_of_ticket` VARCHAR(50) NULL,
  `due_date` DATETIME NULL,
  `lastmodifiedby_id` BIGINT(20) NULL,
  `lastmodified_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_b_office_id_idx` (`office_id` ASC),
  INDEX `fk_b_tm_assgn_idx` (`createdby_id` ASC),
  INDEX `fk_b_tm_usr_idx` (`assigned_to` ASC),
  CONSTRAINT `fk_b_office_id`
    FOREIGN KEY (`office_id`)
    REFERENCES `obstenant-default`.`m_office` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_b_tm_assgn`
    FOREIGN KEY (`createdby_id`)
    REFERENCES `obstenant-default`.`m_appuser` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_b_tm_usr`
    FOREIGN KEY (`assigned_to`)
    REFERENCES `obstenant-default`.`m_appuser` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE if not exists `b_office_ticket_detail` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `ticket_id` BIGINT(20) NOT NULL,
  `comments` TEXT NULL,
  `attachments` VARCHAR(200) NULL,
  `assigned_to` BIGINT(20) NULL,
  `createdby_id` BIGINT(20) NULL,
  `created_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_o_td_usr_idx` (`createdby_id` ASC),
  INDEX `fk_b_office_ticket_detail_1_idx` (`ticket_id` ASC),
  CONSTRAINT `fk_o_td_usr`
    FOREIGN KEY (`createdby_id`)
    REFERENCES `obstenant-default`.`m_appuser` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_b_office_ticket_detail_1`
    FOREIGN KEY (`ticket_id`)
    REFERENCES `obstenant-default`.`b_office_ticket` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

INSERT ignore INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`) VALUES ('CRM', 'CREATE_OFFICETICKET', 'OFFICETICKET', 'CREATE');

INSERT ignore INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('CRM', 'UPDATE_OFFICETICKET', 'OFFICETICKET', 'UPDATE', '0');

INSERT ignore INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`) VALUES ('CRM', 'CLOSE_OFFICETICKET', 'OFFICETICKET', 'CLOSE');


INSERT ignore INTO `m_code` (`code_name`, `is_system_defined`,`code_description`) VALUES ('Swap Device Reasons', '0','Reasons for Swap Device');
SET @a_sc:=(select id from m_code where code_name='Swap Device Reasons');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@a_sc, 'Device Faulty', '1');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@a_sc, 'Service Upgradation', '2');


ALTER TABLE `b_allocation` 
ADD COLUMN `remarks` VARCHAR(30) NULL AFTER `is_deleted`;



