Drop procedure IF EXISTS productId;
DELIMITER //
create procedure productId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'product_id'
     and TABLE_NAME = 'b_prd_ch_mapping'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_prd_ch_mapping` 
CHANGE COLUMN `service_id` `product_id` INT(20) NOT NULL ;
END IF;
END //
DELIMITER ;
call productId();
Drop procedure IF EXISTS productId;



ALTER TABLE `b_prd_ch_mapping`
ADD UNIQUE KEY `unique_prId_chId`(`product_id`,`channel_id`);

