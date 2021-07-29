Drop procedure IF EXISTS addVoucherBatchType;
DELIMITER //
create procedure addVoucherBatchType() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'batch_type'
     and TABLE_NAME = 'b_pin_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_pin_master add column batch_type VARCHAR(45) NULL DEFAULT NULL  AFTER office_id;

END IF;
END //
DELIMITER ;
call addVoucherBatchType();
Drop procedure IF EXISTS addVoucherBatchType;

alter table b_client_register modify generate_key varchar(100) NOT NULL;