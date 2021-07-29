Drop procedure IF EXISTS paramNotes;
DELIMITER //
create procedure paramNotes() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'param_notes'
     and TABLE_NAME = 'b_command_parameters'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_command_parameters`
ADD COLUMN `param_notes` VARCHAR(255) NULL DEFAULT NULL AFTER `param_length`;
END IF;
END //
DELIMITER ;
call paramNotes();
Drop procedure IF EXISTS paramNotes;
