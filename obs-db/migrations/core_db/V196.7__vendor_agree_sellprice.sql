Drop procedure IF EXISTS addsellprice;
DELIMITER //
create procedure addsellprice() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'content_sellprice'
     and TABLE_NAME = 'b_vendor_agmt_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_vendor_agmt_detail ADD content_sellprice decimal(10,2) DEFAULT NULL AFTER content_cost;

END IF;
END //
DELIMITER ;
call addsellprice();
Drop procedure IF EXISTS addsellprice;


