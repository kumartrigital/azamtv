Drop procedure IF EXISTS dropUniqueProductIdChannelId;
DELIMITER //
create procedure dropUniqueProductIdChannelId() 
Begin
   IF EXISTS (
        SELECT *
	FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
	WHERE TC.TABLE_NAME = 'b_prd_ch_mapping'
	AND TC.CONSTRAINT_TYPE = 'UNIQUE')THEN
ALTER TABLE `b_prd_ch_mapping`
DROP INDEX `unique_prId_chId` ;
END IF;
END //
DELIMITER ;
call dropUniqueProductIdChannelId();
Drop procedure IF EXISTS dropUniqueProductIdChannelId;


Drop procedure IF EXISTS UniqueProductIdChannelId;
DELIMITER //
create procedure UniqueProductIdChannelId() 
Begin
  IF NOT EXISTS (
     SELECT *
	FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
	WHERE TC.TABLE_NAME = 'b_prd_ch_mapping'
	AND TC.CONSTRAINT_TYPE = 'UNIQUE')THEN
ALTER TABLE b_prd_ch_mapping
ADD UNIQUE INDEX `unique_prId_chId` (`product_id`, `channel_id`, `is_deleted`);
END IF;
END //
DELIMITER ;
call UniqueProductIdChannelId();
Drop procedure IF EXISTS UniqueProductIdChannelId;




Drop procedure IF EXISTS UniqueUserIdCatalogeId;
DELIMITER //
create procedure UniqueUserIdCatalogeId()
Begin
  IF EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
    WHERE TC.TABLE_NAME = 'b_user_cataloge'
    AND TC.CONSTRAINT_TYPE = 'UNIQUE')THEN
ALTER TABLE `b_user_cataloge`
DROP INDEX `UNIQUE_userId_catalogeId`;
END IF;
END //
DELIMITER ;
call UniqueUserIdCatalogeId();
Drop procedure IF EXISTS UniqueUserIdCatalogeId;



Drop procedure IF EXISTS UniqueUserIdCatalogeIds;
DELIMITER //
create procedure UniqueUserIdCatalogeIds()
Begin
  IF NOT EXISTS (
     SELECT *
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
WHERE TC.TABLE_NAME = 'b_user_cataloge'
AND TC.CONSTRAINT_TYPE = 'UNIQUE')THEN
ALTER TABLE b_user_cataloge
ADD UNIQUE INDEX `UNIQUE_userId_catalogeId` (`user_id`,`cataloge_id`,`is_deleted`);
END IF;
END //
DELIMITER ;
call UniqueUserIdCatalogeIds();
Drop procedure IF EXISTS UniqueUserIdCatalogeIds;
