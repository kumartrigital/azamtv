Drop procedure IF EXISTS adddurationId;
DELIMITER //
create procedure adddurationId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'duration_id'
     and TABLE_NAME = 'b_vendor_agmt_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_vendor_agmt_detail ADD duration_id int(20) DEFAULT NULL AFTER price_region;

END IF;
END //
DELIMITER ;
call adddurationId();
Drop procedure IF EXISTS adddurationId;


