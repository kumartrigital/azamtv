Drop procedure IF EXISTS configurationalter; 
DELIMITER //
create procedure configurationalter() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'name' and COLUMN_KEY != 'UNI' and IS_NULLABLE ='YES'
     and TABLE_NAME = 'c_configuration'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `c_configuration` MODIFY name varchar(50) NOT NULL;
ALTER TABLE `c_configuration` ADD CONSTRAINT `name_config` UNIQUE (`name`);
END IF;
END //
DELIMITER ;
call configurationalter();
Drop procedure IF EXISTS configurationalter;
