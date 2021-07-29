

INSERT IGNORE INTO `m_permission` ( `grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('CRM', 'DELETE_QUOTATION', 'QUOTES', 'DELETE', '0');



INSERT IGNORE INTO `m_permission` ( `grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('CRM', 'UPDATE_QUOTATION', 'QUOTES', 'UPDATE', '0');







Drop procedure IF EXISTS chargeCode;
DELIMITER //
create procedure chargeCode() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'charge_code'
     and TABLE_NAME = 'b_quote_order'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_quote_order` 
ADD COLUMN `charge_code` VARCHAR(45) NULL DEFAULT NULL AFTER `plan_onetime_charge`;
END IF;
END //
DELIMITER ;
call chargeCode();
Drop procedure IF EXISTS chargeCode;
