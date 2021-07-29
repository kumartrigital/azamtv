Drop procedure IF EXISTS systemCodeUnique;
DELIMITER //
create procedure systemCodeUnique()
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'system_code'
     and TABLE_NAME = 'b_network_element'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_network_element`
DROP INDEX `system_code_UNIQUE`;
END IF;
END //
DELIMITER ;
call systemCodeUnique();
Drop procedure IF EXISTS systemCodeUnique;


Drop procedure IF EXISTS systemCodeUniqueKeyAdd;
DELIMITER //
create procedure systemCodeUniqueKeyAdd()
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'system_code'
     and TABLE_NAME = 'b_network_element'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_network_element`
ADD UNIQUE INDEX `system_code_UNIQUE` (`system_code` ASC, `is_deleted` ASC);
END IF;
END //
DELIMITER ;
call systemCodeUniqueKeyAdd();
Drop procedure IF EXISTS systemCodeUniqueKeyAdd;
