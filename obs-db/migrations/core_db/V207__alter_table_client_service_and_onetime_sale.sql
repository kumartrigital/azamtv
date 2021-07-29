Drop procedure IF EXISTS addIsDeletedInClientService;
DELIMITER //
create procedure addIsDeletedInClientService() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_deleted'
     and TABLE_NAME = 'b_client_service'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_client_service add column is_deleted char(1) default 'n' after `status`;

END IF;
END //
DELIMITER ;
call addIsDeletedInClientService();
Drop procedure IF EXISTS addIsDeletedInClientService;



Drop procedure IF EXISTS addclientserviceid;
DELIMITER //
create procedure addclientserviceid() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'client_service_id'
     and TABLE_NAME = 'b_onetime_sale'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_onetime_sale add column client_service_id char(1) not null after `item_id`;

END IF;
END //
DELIMITER ;
call addclientserviceid();
Drop procedure IF EXISTS addclientserviceid;


Drop procedure IF EXISTS addProvisioningId;
DELIMITER //
create procedure addProvisioningId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'provisioning_id'
     and TABLE_NAME = 'b_hw_plan_mapping'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_hw_plan_mapping add column provisioning_id INT(10) NOT NULL AFTER `plan_code`;

END IF;
END //
DELIMITER ;
call addProvisioningId();
Drop procedure IF EXISTS addProvisioningId;


