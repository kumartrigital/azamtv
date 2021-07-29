
Drop procedure IF EXISTS UniqueUserIdCatalogeId;
DELIMITER //
create procedure UniqueUserIdCatalogeId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'user_id'
     and TABLE_NAME = 'b_user_cataloge'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_user_cataloge` 
ADD UNIQUE INDEX `UNIQUE_userId_catalogeId` (`user_id` ASC, `cataloge_id` ASC, `is_deleted` ASC);
END IF;
END //
DELIMITER ;
call UniqueUserIdCatalogeId();
Drop procedure IF EXISTS UniqueUserIdCatalogeId;



