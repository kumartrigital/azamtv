INSERT IGNORE INTO job VALUES(null, 'EXPORTDATA','Export Data','0 30 23 1/1 * ? *', 'Daily once at Midnight', '0001-01-01 00:00:00', '5', NULL, '2014-12-24 17:40:12', NULL, 'EXPORTDATAJobDetaildefault _ DEFAULT', NULL, '0', '0', '1', '0', '0', '1');

SET @jobId=(select id from job where name='EXPORTDATA');

INSERT IGNORE INTO job_parameters VALUES(null, @jobId, 'processDate', 'DATE', 'NOW()', '26 December 2014', 'Y', NULL);

INSERT IGNORE INTO job_parameters VALUES(null, @jobId, 'reportName', 'COMBO', 'ALL', 'Export Data', 'Y', NULL);

Drop procedure IF EXISTS pinMaster; 
DELIMITER //
create procedure pinMaster() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE  COLUMN_NAME ='office_id' 
       and TABLE_NAME = 'b_pin_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_pin_master ADD office_id bigint(20) NOT NULL DEFAULT '1';
END IF;
END //
DELIMITER ;
call pinMaster();
Drop procedure IF EXISTS pinMaster;


Drop procedure IF EXISTS pinMasterBatch; 
DELIMITER //
create procedure pinMasterBatch() 
Begin
IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE  COLUMN_NAME ='batch_description' 
       and TABLE_NAME = 'b_pin_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_pin_master DROP batch_description;
END IF;
END //
DELIMITER ;
call pinMasterBatch();
Drop procedure IF EXISTS pinMasterBatch;

Drop procedure IF EXISTS pinDetails; 
DELIMITER //
create procedure pinDetails() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE  COLUMN_NAME ='status' 
       and TABLE_NAME = 'b_pin_details'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_pin_details ADD status varchar(50) NOT NULL DEFAULT 'NEW';
END IF;
END //
DELIMITER ;
call pinDetails();
Drop procedure IF EXISTS pinDetails;



 


