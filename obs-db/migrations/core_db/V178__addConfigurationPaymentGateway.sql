Drop procedure IF EXISTS configurationValue; 
DELIMITER //
create procedure configurationValue() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'value'
     and TABLE_NAME = 'c_configuration'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE c_configuration change COLUMN `value` `value` LONGTEXT  CHARACTER SET utf8 DEFAULT NULL;
END IF;
END //
DELIMITER ;
call configurationValue();
Drop procedure IF EXISTS configurationValue;

insert ignore into c_configuration values(null,'payment-email-description',0,'[{"value":"Success","result":"Success","response":"Transaction Success"},
{"value":"Failure","result":"Failure","response":"Transaction Rejected"},{"value":"Pending","result":"Pending","response":"Transaction Pending"},
{"value":"Decline","result":"Decline","response":"Transaction Already Exist with This Id"}]');

DELIMITER //
create procedure addlicensekeyTenants() 
Begin
IF EXISTS ( SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'obsplatform-tenants') THEN

IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'license_key'
     and TABLE_NAME = 'tenants'
     and TABLE_SCHEMA = 'obsplatform-tenants')THEN
Alter  table tenants add column license_key varchar(50) DEFAULT NULL;
END IF;

END IF;
END //
DELIMITER ;
call addlicensekeyTenants();
Drop procedure IF EXISTS addlicensekeyTenants; 
