Drop procedure IF EXISTS addIsProvisioning;
DELIMITER //
create procedure addIsProvisioning() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_provisioning'
     and TABLE_NAME = 'b_item_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_item_master add column is_provisioning char(1) NULL default NULL after `supplier_id`;

END IF;
END //
DELIMITER ;
call addIsProvisioning();
Drop procedure IF EXISTS addIsProvisioning;
