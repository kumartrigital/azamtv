Drop procedure IF EXISTS billmaster; 
DELIMITER //
create procedure billmaster() 
Begin
 IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'adjustments_payments'
     and TABLE_NAME = 'b_bill_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_bill_master drop column adjustments_payments;
END IF;

IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'bill_period'
     and TABLE_NAME = 'b_bill_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_bill_master drop column bill_period;
END IF;


IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'group_id'
     and TABLE_NAME = 'b_bill_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_bill_master drop column group_id;
END IF;

IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'Parent_id'
     and TABLE_NAME = 'b_bill_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_bill_master add column Parent_id bigint(20) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call billmaster();
Drop procedure IF EXISTS billmaster;  




