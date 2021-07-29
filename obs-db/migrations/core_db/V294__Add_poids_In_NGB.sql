Drop procedure IF EXISTS addpoidinplanmaster;
DELIMITER //
create procedure addpoidinplanmaster() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'plan_poid'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
     ALTER TABLE b_plan_master 
     add COLUMN plan_poid varchar(10) ;

END IF;
END //
DELIMITER ;
call addpoidinplanmaster();
Drop procedure IF EXISTS addpoidinplanmaster;




Drop procedure IF EXISTS addpoidinplandetail;
DELIMITER //
create procedure addpoidinplandetail() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'deal_poid'
     and TABLE_NAME = 'b_plan_detail'
     and TABLE_SCHEMA = DATABASE())THEN
     ALTER TABLE `b_plan_detail` 
     ADD COLUMN `deal_poid` VARCHAR(10) ;

END IF;
END //
DELIMITER ;
call addpoidinplandetail();
Drop procedure IF EXISTS addpoidinplandetail;

Drop procedure IF EXISTS addpoidinservice;
DELIMITER //
create procedure addpoidinservice() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'product_poid'
     and TABLE_NAME = 'b_product'
     and TABLE_SCHEMA = DATABASE())THEN
     ALTER TABLE `b_product` 
     ADD COLUMN `product_poid` VARCHAR(10)  ;

END IF;
END //
DELIMITER ;
call addpoidinservice();
Drop procedure IF EXISTS addpoidinservice;

Drop procedure IF EXISTS addpriority;
DELIMITER //
create procedure addpriority() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'priority'
     and TABLE_NAME = 'b_product'
     and TABLE_SCHEMA = DATABASE())THEN
     ALTER TABLE `b_product` 
     ADD COLUMN `priority` Int(3)  ;

END IF;
END //
DELIMITER ;
call addpriority();
Drop procedure IF EXISTS addpriority;

