Drop procedure IF EXISTS bcmdukadding;
DELIMITER //
create procedure bcmdukadding()
Begin
  IF NOT EXISTS (
   SELECT * FROM information_schema.COLUMNS
WHERE COLUMN_NAME = 'is_Bouquet'
AND TABLE_NAME = 'b_product'
AND TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_product`
ADD COLUMN `is_Bouquet` CHAR(1) NULL DEFAULT 'N' AFTER `product_category`;
END IF;
END //
DELIMITER ;
call bcmdukadding();
Drop procedure IF EXISTS bcmdukadding;


