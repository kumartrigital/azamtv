INSERT ignore INTO `m_code` (`code_name`, `is_system_defined`, `code_description`, `module`) VALUES ('Identification Proofs', '0', 'Identification Proof which are  provided and authorized by Government', 'Client');
SET @a_sc:=(select id from m_code where code_name='Identification Proofs');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@a_sc, 'Aadhar Card', '1');
INSERT ignore INTO `m_code_value` ( `code_id`, `code_value`, `order_position`) VALUES (@a_sc, 'Voter Id', '2');


Drop procedure IF EXISTS IndexIdKey;
DELIMITER //
create procedure IndexIdKey() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'id'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_client` 
ADD COLUMN `id_key` INT(11) NULL AFTER `po_id`,
ADD COLUMN `id_value` VARCHAR(50) NULL AFTER `id_key`,
ADD INDEX `fk_m_client_m_code_idx` (`id_key` ASC);
ALTER TABLE `m_client` 
ADD CONSTRAINT `fk_m_client_m_code_id`
  FOREIGN KEY (`id_key`)
  REFERENCES `m_code_value` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
END IF;
END //
DELIMITER ;
call IndexIdKey();
Drop procedure IF EXISTS IndexIdKey;


Drop procedure IF EXISTS IndexDasType;
DELIMITER //
create procedure IndexDasType() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'id'
     and TABLE_NAME = 'm_office'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_office` 
ADD COLUMN `das_type` INT(11) NULL AFTER `Payment_Type`,
ADD INDEX `fk_m_office_m_code_das_idx` (`das_type` ASC);
ALTER TABLE `m_office` 
ADD CONSTRAINT `fk_m_office_m_code_das`
  FOREIGN KEY (`das_type`)
  REFERENCES `m_code_value` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
END IF;
END //
DELIMITER ;
call IndexDasType();
Drop procedure IF EXISTS IndexDasType;




INSERT ignore INTO `m_code` (`code_name`, `is_system_defined`, `code_description`) VALUES ('das_type', '0', 'For Organization Purpose');
SET @a_sc2:=(select id from m_code where code_name='das_type');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@a_sc2, 'DAS-I', '1');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@a_sc2, 'DAS-II', '2');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@a_sc2, 'DAS-III', '3');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@a_sc2, 'DAS-IV', '4');

