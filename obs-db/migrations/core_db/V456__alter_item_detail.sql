
create index idx_deit_model on b_item_detail(item_model);
create index idx_sr_no on b_item_pairing(serial_no_1);
create index idx_is_del on b_item_pairing(is_deleted);
drop index idx_clid ON b_item_detail;
drop index idx_itemdoid ON b_item_detail;

Drop procedure IF EXISTS itemDetailSerialNo;
DELIMITER //
create procedure itemDetailSerialNo() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'serial_no'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_item_detail
MODIFY `serial_no` varchar(100) NOT NULL;
END IF;
END //
DELIMITER ;
call itemDetailSerialNo();
Drop procedure IF EXISTS itemDetailSerialNo;
