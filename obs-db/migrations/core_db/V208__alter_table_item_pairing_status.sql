Drop procedure IF EXISTS changeItemPairingTable;
DELIMITER //
create procedure changeItemPairingTable() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'status'
     and TABLE_NAME = 'b_item_pairing'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_item_pairing change column status status VARCHAR(15) DEFAULT NULL;

END IF;
END //
DELIMITER ;
call changeItemPairingTable();
Drop procedure IF EXISTS changeItemPairingTable;


