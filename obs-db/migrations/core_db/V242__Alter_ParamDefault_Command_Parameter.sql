
Drop procedure IF EXISTS paramdefaultincrement;
DELIMITER //
create procedure paramdefaultincrement() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'param_default'
     and TABLE_NAME = 'b_command_parameters'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_command_parameters` 
CHANGE COLUMN `param_default` `param_default` VARCHAR(200) NULL DEFAULT NULL ;

END IF;
END //
DELIMITER ;
call paramdefaultincrement();
Drop procedure IF EXISTS paramdefaultincrement;

