SET SQL_SAFE_UPDATES = 0;
update c_configuration set name='rounding' where name='Rounding';
update c_configuration set name='implicit-association' where name='Implicit Association';
update c_configuration set name='date-format' where name='DateFormat';
update c_configuration set name='login' where name='Login';
update c_configuration set name='forcible-balance-check' where name='Forcible Balance Check';
update c_configuration set name='device-agrement-type' where name='CPE_TYPE';
update c_configuration set name='is-paypal' where name='Is_Paypal';
update c_configuration set name='test' where name='TEST';
update c_configuration set name='active-devices' where name='Active Devices';
-- update c_configuration set name='constraint-approach-for-datatables' where name='constraint_approach_for_datatables';
update c_configuration set name='register-plan' where name='Register_plan';
update c_configuration set name='disconnection-credit' where name='Disconnection Credit';
update c_configuration set name='registration-requires-device' where name='Registration_requires_device';
update c_configuration set name='change-plan-align-dates', enabled=0  where name='CHANGE_PLAN_ALIGN_DATES';
delete from  c_configuration where name='is_selfcareuser';
update c_configuration set name='selfcare-requires-email' where name='Selfcare_requires_email';
update c_configuration set name='smtp' where name='SMTP';
update c_configuration set name='active-viewers' where name='Active Viewers';
update c_configuration set name='change-plan-align-dates' where name='CHANGE_PLAN_ALIGN_DATES';
update c_configuration set name='osd-provisioningSystem' where name='OSD_ProvisioningSystem';
update c_configuration set name='is-paypal-for-ios' where name='Is_Paypal_For_Ios';
update c_configuration set name='is-cache-enabled' where name='Is Cache Enabled';

Drop procedure IF EXISTS alteruploadTablename; 
DELIMITER //
create procedure alteruploadTablename() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'uploads_status'
     and TABLE_SCHEMA = DATABASE())THEN
rename table uploads_status to b_data_uploads;
END IF;
END //
DELIMITER ;
call alteruploadTablename();
Drop procedure IF EXISTS alteruploadTablename;


Drop procedure IF EXISTS addUniqueInGrn; 
DELIMITER //
create procedure addUniqueInGrn() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'po_no' and COLUMN_KEY != 'UNI' and IS_NULLABLE ='YES'
     and TABLE_NAME = 'b_grn'
     and TABLE_SCHEMA = DATABASE())THEN
alter TABLE b_grn add constraint UNIQUE KEY `purchase_no_code_key` (`po_no`);
END IF;
END //
DELIMITER ;
call addUniqueInGrn();
Drop procedure IF EXISTS addUniqueInGrn;

SET SQL_SAFE_UPDATES = 1;
