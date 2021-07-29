Drop procedure IF EXISTS addcscolumninallocation;
DELIMITER //
create procedure addcscolumninallocation() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'clientservice_id'
     and TABLE_NAME = 'b_allocation'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_allocation` 
ADD COLUMN `clientservice_id` BIGINT(10) NOT NULL AFTER `allocation_date`;
END IF;
END //
DELIMITER ;
call addcscolumninallocation();
Drop procedure IF EXISTS addcscolumninallocation;



Drop procedure IF EXISTS addordertypeinallocation;
DELIMITER //
create procedure addordertypeinallocation() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'order_type'
     and TABLE_NAME = 'b_allocation'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_allocation` 
ADD COLUMN `order_type` VARCHAR(20) NOT NULL AFTER `clientservice_id`;
END IF;
END //
DELIMITER ;
call addordertypeinallocation();
Drop procedure IF EXISTS addordertypeinallocation;


INSERT IGNORE INTO `m_code`(id,code_name,is_system_defined,code_description) 
VALUES(null,'NUMBER GENERATOR',0,'Custom Number Generator');
SET @a_lid:=(select id from m_code where code_name='NUMBER GENERATOR');
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'CR-',1);
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'OR-',2);

