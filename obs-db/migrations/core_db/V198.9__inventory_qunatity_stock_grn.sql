Drop procedure IF EXISTS addstockquantity;
DELIMITER //
create procedure addstockquantity() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'stock_quantity'
     and TABLE_NAME = 'b_grn'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_grn` ADD `stock_quantity` bigint(20) NOT NULL AFTER `received_quantity`;

END IF;
END //
DELIMITER ;
call addstockquantity();
Drop procedure IF EXISTS addstockquantity;


SET SQL_SAFE_UPDATES=0;
Drop procedure IF EXISTS addreceivequantity;
DELIMITER //
create procedure addreceivequantity() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'received_quantity'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_item_detail` ADD `received_quantity` bigint(20) NOT NULL AFTER `quality`;
UPDATE `b_item_detail` SET `received_quantity` = 1;

END IF;
END //
DELIMITER ;
call addreceivequantity();
Drop procedure IF EXISTS addreceivequantity;


ALTER TABLE b_item_detail MODIFY `serial_no` varchar(100) DEFAULT NULL;

