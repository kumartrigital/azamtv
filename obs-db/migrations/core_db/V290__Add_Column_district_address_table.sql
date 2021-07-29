Drop procedure IF EXISTS districts1;
DELIMITER //
create procedure districts1() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'district'
     and TABLE_NAME = 'b_office_address'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_office_address`  
ADD COLUMN `district` VARCHAR(100) NULL DEFAULT NULL AFTER `office_id`;

END IF;
END //
DELIMITER ;
call districts1();
Drop procedure IF EXISTS districts1;




Drop procedure IF EXISTS districts2;
DELIMITER //
create procedure districts2() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'district'
     and TABLE_NAME = 'b_client_address'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_client_address`  
ADD COLUMN `district` VARCHAR(200) NULL DEFAULT NULL AFTER `phone_num`;

END IF;
END //
DELIMITER ;
call districts2();
Drop procedure IF EXISTS districts2;





Drop procedure IF EXISTS districts3;
DELIMITER //
create procedure districts3() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'district'
     and TABLE_NAME = 'b_property_defination'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_property_defination`  
ADD COLUMN `district` VARCHAR(20) NULL DEFAULT NULL AFTER `lastmodifiedby_id`;

END IF;
END //
DELIMITER ;
call districts3();
Drop procedure IF EXISTS districts3;















