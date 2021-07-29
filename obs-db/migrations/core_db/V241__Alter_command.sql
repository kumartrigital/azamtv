Drop procedure IF EXISTS bcmdukadding;
DELIMITER //
create procedure bcmdukadding() 
Begin
  IF NOT EXISTS (
     SELECT *
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
WHERE TC.TABLE_NAME = 'b_command'
AND TC.CONSTRAINT_TYPE = 'UNIQUE')THEN
ALTER TABLE `b_command` 
ADD UNIQUE INDEX `uk_bcmd_provsys_cmdname` (`provisioning_system` ASC, `command_name` ASC);

END IF;
END //
DELIMITER ;
call bcmdukadding();
Drop procedure IF EXISTS bcmdukadding;












