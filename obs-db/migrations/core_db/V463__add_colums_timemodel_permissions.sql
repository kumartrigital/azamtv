INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'UPDATE_TIMEMODEL', 'TIMEMODEL', 'UPDATE', '0');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'DELETE_TIMEMODEL', 'TIMEMODEL', 'DELETE', '0');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'UPDATE_TIMEPERIOD ', 'TIMEPERIOD', 'UPDATE', '0');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('collectionbatch', 'DELETE_TIMEPERIOD ', 'TIMEPERIOD', 'DELETE', '0');







Drop procedure IF EXISTS isDelete;
DELIMITER //
create procedure isDelete() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_deleted'
     and TABLE_NAME = 'r_time_period'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `r_time_period`
ADD COLUMN `is_deleted` CHAR(1) NOT NULL AFTER `endtime`;
END IF;
END //
DELIMITER ;
call isDelete();
Drop procedure IF EXISTS isDelete;

Drop procedure IF EXISTS isActive;
DELIMITER //
create procedure isActive() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_active'
     and TABLE_NAME = 'r_timemodel'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `r_timemodel`
ADD COLUMN `is_active` CHAR(1) NOT NULL AFTER `description`;
END IF;
END //
DELIMITER ;
call isActive();
Drop procedure IF EXISTS isActive;

Drop procedure IF EXISTS startMonth;
DELIMITER //
create procedure startMonth()
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'startmonth'
     and TABLE_NAME = 'r_time_period'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `r_time_period`
CHANGE COLUMN `startmonth` `startmonth` VARCHAR(10) NULL DEFAULT '0' ;
END IF;
END //
DELIMITER ;
call startMonth();
Drop procedure IF EXISTS startMonth;


Drop procedure IF EXISTS endMonth;
DELIMITER //
create procedure endMonth() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'endmonth'
     and TABLE_NAME = 'r_time_period'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `r_time_period`
CHANGE COLUMN `endmonth` `endmonth` VARCHAR(10) NULL DEFAULT '0' ;
END IF;
END //
DELIMITER ;
call endMonth();
Drop procedure IF EXISTS endMonth;


Drop procedure IF EXISTS startTime;
DELIMITER //
create procedure startTime() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'starttime'
     and TABLE_NAME = 'r_time_period'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `r_time_period`
CHANGE COLUMN `starttime` `starttime` VARCHAR(10) NULL DEFAULT '00:00:00' ;
END IF;
END //
DELIMITER ;
call startTime();
Drop procedure IF EXISTS startTime;


Drop procedure IF EXISTS endTime;
DELIMITER //
create procedure endTime() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'endtime'
     and TABLE_NAME = 'r_time_period'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `r_time_period`
CHANGE COLUMN `endtime` `endtime` VARCHAR(10) NULL DEFAULT '00:00:00' ;
END IF;
END //
DELIMITER ;
call endTime();
Drop procedure IF EXISTS endTime;


