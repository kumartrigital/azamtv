Drop procedure IF EXISTS timeTaken;
DELIMITER //
create procedure timeTaken() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'time_taken'
     and TABLE_NAME = 'b_sub_category'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_sub_category` 
ADD COLUMN `time_taken` INT(20) NOT NULL ;
END IF;
END //
DELIMITER ;
call timeTaken();
Drop procedure IF EXISTS timeTaken;

insert ignore into m_permission values(null,'CRM','CREATE_SUBCATEGORY','SUBCATEGORY','CREATE',0);


