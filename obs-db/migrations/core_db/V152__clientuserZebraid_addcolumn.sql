Drop procedure IF EXISTS clientuserZebra; 
DELIMITER //
create procedure clientuserZebra() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'zebra_subscriber_id'
     and TABLE_NAME = 'b_clientuser'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table b_clientuser ADD COLUMN zebra_subscriber_id bigint(20)  DEFAULT NULL;
END IF;
END //
DELIMITER ;
call clientuserZebra();
Drop procedure IF EXISTS clientuserZebra; 

insert ignore into m_code_value values(null,13,'ZebraOTT',6);

Drop procedure IF EXISTS processRequstMsg;
DELIMITER //
create procedure processRequstMsg() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'sent_message' and DATA_TYPE='varchar' 
     and TABLE_NAME = 'b_process_request_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_process_request_detail CHANGE `sent_message` `sent_message` TEXT NOT NULL;
END IF;
END //
DELIMITER ;
call processRequstMsg();
Drop procedure IF EXISTS processRequstMsg; 


Drop procedure IF EXISTS clientImage; 
DELIMITER //
create procedure clientImage() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'image_key' and DATA_TYPE='bigint' 
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE m_client DROP FOREIGN KEY `FK_m_client_m_image`;
ALTER TABLE m_client CHANGE COLUMN `image_key` `image_key` VARCHAR(250) NULL DEFAULT NULL;
END IF;
END //
DELIMITER ;
call clientImage();
Drop procedure IF EXISTS clientImage; 
