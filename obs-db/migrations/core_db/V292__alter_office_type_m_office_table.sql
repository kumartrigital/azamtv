 

Drop procedure IF EXISTS AddOfficeType;
DELIMITER //
create procedure AddOfficeType() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'office_type'
     and TABLE_NAME = 'm_office'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE`m_office` 
CHANGE COLUMN `office_type` `office_type` VARCHAR(10) NOT NULL DEFAULT '1' ;

END IF;
END //
DELIMITER ;
call AddOfficeType();
Drop procedure IF EXISTS AddOfficeType;
