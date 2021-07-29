Drop procedure IF EXISTS teamemail;
DELIMITER //
create procedure teamemail() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'team_email'
     and TABLE_NAME = 'b_team'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_team` 
ADD COLUMN `team_email` VARCHAR(45) NULL DEFAULT NULL AFTER `is_deleted`;
END IF;
END //
DELIMITER ;
call teamemail();
Drop procedure IF EXISTS teamemail;  

