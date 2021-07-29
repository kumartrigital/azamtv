Drop procedure IF EXISTS provisioningRequestStatusChanged;
DELIMITER //
create procedure provisioningRequestStatusChanged() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'status'
     and TABLE_NAME = 'b_provisioning_request'
     and TABLE_SCHEMA = DATABASE()) THEN
ALTER TABLE `b_provisioning_request` CHANGE COLUMN `status` `status` CHAR(1) NOT NULL DEFAULT 'N' ;
END IF;
END //
DELIMITER ;
call provisioningRequestStatusChanged();
Drop procedure IF EXISTS provisioningRequestStatusChanged;
