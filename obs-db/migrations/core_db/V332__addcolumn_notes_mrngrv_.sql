Drop procedure IF EXISTS newnote;
DELIMITER //
create procedure newnote()
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'notes'
     and TABLE_NAME = 'b_grv'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_grv`
ADD COLUMN `notes` VARCHAR(200) NULL DEFAULT NULL AFTER `lastmodified_date`;
END IF;
END //
DELIMITER ;
call newnote();
Drop procedure IF EXISTS newnote;



Drop procedure IF EXISTS newnote1;
DELIMITER //
create procedure newnote1()
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'notes'
     and TABLE_NAME = 'b_mrn'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_mrn`
ADD COLUMN `notes` VARCHAR(200) NULL DEFAULT NULL AFTER `lastmodified_date`;
END IF;
END //
DELIMITER ;
call newnote1();
Drop procedure IF EXISTS newnote1;
