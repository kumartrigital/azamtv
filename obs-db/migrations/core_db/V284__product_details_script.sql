Drop procedure IF EXISTS dropproduct;
DELIMITER //
create procedure dropproduct() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'validity_end'
     and TABLE_NAME = 'b_product'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_product` 
DROP COLUMN `validity_end`,
DROP COLUMN `validity_start`,
DROP COLUMN `provision_id`;
END IF;
END //
DELIMITER ;
call dropproduct();
Drop procedure IF EXISTS dropproduct;



CREATE TABLE if not exists`b_product_detail` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT(20) NOT NULL,
  `param_type` VARCHAR(15) NOT NULL,
  `param_name` VARCHAR(50) NOT NULL,
  `param_value` VARCHAR(200) NOT NULL,
  `param_category` VARCHAR(1) NULL DEFAULT NULL,
  `is_deleted` CHAR(1) NULL DEFAULT 'N',
  PRIMARY KEY (`id`));


Drop procedure IF EXISTS plandetail;
DELIMITER //
create procedure plandetail() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_code'
     and TABLE_NAME = 'b_plan_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_detail` 
CHANGE COLUMN `service_code` `product_id` VARCHAR(10) NULL DEFAULT NULL ;END IF;
END //
DELIMITER ;
call plandetail();
Drop procedure IF EXISTS plandetail;

Drop procedure IF EXISTS planpricing;
DELIMITER //
create procedure planpricing() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_code'
     and TABLE_NAME = 'b_plan_pricing'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_pricing` 
CHANGE COLUMN `service_code` `product_id` BIGINT(20) NULL DEFAULT NULL ;END IF;
END //
DELIMITER ;
call planpricing();
Drop procedure IF EXISTS planpricing;

Drop procedure IF EXISTS orderpricing;
DELIMITER //
create procedure orderpricing() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_id'
     and TABLE_NAME = 'b_order_price'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_order_price` 
CHANGE COLUMN `service_id` `product_id` INT(20) NOT NULL ;END IF;
END //
DELIMITER ;
call orderpricing();
Drop procedure IF EXISTS orderpricing;



ALTER TABLE `b_order_line` 
DROP FOREIGN KEY `fk_ol_sid`;



Drop procedure IF EXISTS orderlinechange;
DELIMITER //
create procedure orderlinechange() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_id'
     and TABLE_NAME = 'b_order_line'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_order_line`
CHANGE COLUMN `service_id` `product_id` INT(20) NOT NULL ;END IF;
END //
DELIMITER ;
call orderlinechange();
Drop procedure IF EXISTS orderlinechange;








