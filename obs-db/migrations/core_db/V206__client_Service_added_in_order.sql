Drop procedure IF EXISTS addClientServiceInOrder;
DELIMITER //
create procedure addClientServiceInOrder() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'client_service_id'
     and TABLE_NAME = 'b_orders'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_orders add column client_service_id BIGINT(1) NOT NULL after `user_action`;

END IF;
END //
DELIMITER ;
call addClientServiceInOrder();
Drop procedure IF EXISTS addClientServiceInOrder;



CREATE TABLE IF NOT EXISTS `b_provisioning_request` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `client_id` BIGINT(20) NOT NULL,
  `order_id` BIGINT(20) NOT NULL,
  `request_type` VARCHAR(45) NOT NULL,
  `provisioning_system` INT(20) NOT NULL,
  `status` CHAR(1) NULL DEFAULT 'N',
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NULL DEFAULT NULL,
  `request_message` VARCHAR(500) NOT NULL,
  `response_message` VARCHAR(500) NULL DEFAULT 'null',
  `response_date` DATETIME NULL DEFAULT NULL,
  `response_status` VARCHAR(45) NULL DEFAULT 'null',
   `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`));

INSERT IGNORE INTO m_permission (`grouping`, `code`, `entity_name`, 
`action_name`, `can_maker_checker`) VALUES ( 'client&orders', 'CREATE_PROVISIONINGREQUEST', 'PROVISIONINGREQUEST', 'CREATE', '0');
