Drop procedure IF EXISTS itemmastermodel; 
DELIMITER //
create procedure itemmastermodel() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_make'
     and TABLE_NAME = 'b_item_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_item_master` drop column item_make;
END IF;
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_model'
     and TABLE_NAME = 'b_item_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_item_master` drop column item_model;
END IF;
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_model'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_item_detail ADD item_model varchar(60) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call itemmastermodel();
Drop procedure IF EXISTS itemmastermodel;
