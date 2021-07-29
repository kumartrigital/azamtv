Drop procedure IF EXISTS removeUniqueIndex;
DELIMITER //
create procedure removeUniqueIndex() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'user_id'
     and TABLE_NAME = 'b_user_cataloge'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_user_cataloge` 
DROP INDEX `uk_usrId_catId` ;

END IF;
END //
DELIMITER ;
call removeUniqueIndex();
Drop procedure IF EXISTS removeUniqueIndex;
