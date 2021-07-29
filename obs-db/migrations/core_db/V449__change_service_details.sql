Drop procedure IF EXISTS serviceDetailParamValue;
DELIMITER //
create procedure serviceDetailParamValue() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'param_value'
     and TABLE_NAME = 'b_service_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_service_detail
CHANGE COLUMN `param_value` `param_value` VARCHAR(200) NULL;
END IF;
END //
DELIMITER ;
call serviceDetailParamValue();
Drop procedure IF EXISTS serviceDetailParamValue;
