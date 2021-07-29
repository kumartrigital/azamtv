Drop procedure IF EXISTS latitudeId;
DELIMITER //
create procedure latitudeId() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'latitude'
     and TABLE_NAME = 'b_clientuser'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_clientuser` 
CHANGE COLUMN `latitude` `latitude` DECIMAL(10,8) NULL DEFAULT NULL ,
CHANGE COLUMN `longitude` `longitude` DECIMAL(11,8) NULL DEFAULT NULL ;
END IF;
END //
DELIMITER ;
call latitudeId();
Drop procedure IF EXISTS latitudeId;



