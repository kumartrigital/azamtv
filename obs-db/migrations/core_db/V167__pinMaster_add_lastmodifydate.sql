Drop procedure if exists pinMasterDate;
DELIMITER //
create procedure pinMasterDate()
BEGIN
IF NOT EXISTS(Select * from INFORMATION_SCHEMA.COLUMNS
where COLUMN_NAME = 'lastmodified_date'
and TABLE_NAME ='b_pin_master' 
and TABLE_SCHEMA = DATABASE())then
Alter table b_pin_master add column lastmodified_date datetime DEFAULT NULL after `created_date`;
END IF ;

IF NOT EXISTS(Select * from INFORMATION_SCHEMA.COLUMNS
where COLUMN_NAME = 'lastmodifiedby_id'
and TABLE_NAME ='b_pin_master' 
and TABLE_SCHEMA = DATABASE())then
Alter table b_pin_master add column lastmodifiedby_id bigint(20) DEFAULT NULL after `created_date`;
END IF ;
END //
DELIMITER
call pinMasterDate();

Drop procedure if exists pinMasterDate;