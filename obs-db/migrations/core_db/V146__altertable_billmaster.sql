DELIMITER //
create procedure cancelbillmaster() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_deleted'
     and TABLE_NAME = 'b_bill_master'
     and TABLE_SCHEMA = DATABASE())THEN
Alter  table b_bill_master add column is_deleted char(2) DEFAULT 'N';
END IF;
END //
DELIMITER ;
call cancelbillmaster();
Drop procedure IF EXISTS cancelbillmaster; 

insert ignore into c_configuration VALUES (null,'include-network-broadcast-ip',1,'true');
