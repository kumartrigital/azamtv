/*ALTER TABLE `b_event_master` MODIFY `charge_code` varchar(10) null;
ALTER TABLE `b_event_master` ADD column `event_category` varchar(50) DEFAULT NULL;*/

Drop procedure IF EXISTS eventmaster; 
DELIMITER //
create procedure eventmaster() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'event_category'
     and TABLE_NAME = 'b_event_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_event_master` ADD column `event_category` varchar(50) DEFAULT NULL;
END IF;
IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'charge_code'
     and TABLE_NAME = 'b_event_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_event_master` MODIFY `charge_code` varchar(10) null;
END IF;
END //
DELIMITER ;
call eventmaster();
Drop procedure IF EXISTS eventmaster;

INSERT IGNORE INTO `m_permission`(grouping,code,entity_name,action_name,can_maker_checker) 
VALUES ('Billing Master','CREATE_MEDIAASSETLOCATIONATTRIBUTES','MEDIAASSETLOCATIONATTRIBUTES','CREATE',1);


Drop procedure IF EXISTS addcpShare; 
DELIMITER //
create procedure addcpShare() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'cp_share'
     and TABLE_NAME = 'b_media_asset'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_media_asset` ADD column `cp_share`  decimal(10,2) DEFAULT '0.00';
END IF;
END //
DELIMITER ;
call addcpShare();
Drop procedure IF EXISTS addcpShare;
