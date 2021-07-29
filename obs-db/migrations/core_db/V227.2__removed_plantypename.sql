Drop procedure IF EXISTS removePlanTypeNameColumn;
DELIMITER //
create procedure removePlanTypeNameColumn() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'plan_type_name'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_master` DROP COLUMN `plan_type_name`;

END IF;
END //
DELIMITER ;
call removePlanTypeNameColumn();
Drop procedure IF EXISTS removePlanTypeNameColumn;




