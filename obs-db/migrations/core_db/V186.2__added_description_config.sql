Drop procedure IF EXISTS moduleToconfig; 
DELIMITER //
create procedure moduleToconfig() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'module'
     and TABLE_NAME = 'c_configuration'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE c_configuration ADD module VARCHAR(60) default NULL;
END IF;
END //
DELIMITER ;
call moduleToconfig();
Drop procedure IF EXISTS moduleToconfig;


Drop procedure IF EXISTS moduleTodescription; 
DELIMITER //
create procedure moduleTodescription() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'description'
     and TABLE_NAME = 'c_configuration'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table c_configuration add column description text;
END IF;
END //
DELIMITER ;
call moduleTodescription();
Drop procedure IF EXISTS moduleTodescription;

set sql_safe_updates=0;
Update c_configuration set module='General',description='It Enables the featuer in General to use the application / form with approval process' where name='maker-checker';
Update c_configuration set module='General',description='Application api will have ability to perform beter when deployed in aws' where name='amazon-S3';
Update c_configuration set module='Client',description='Used in mobile app to check the client balace while we retrive data' where name='balance-check';
Update c_configuration set module='Order',description='By using this flag we can control the Disconnection Credit on change plan if true then dates are alighned else from the date to the end date of first plan we give DC and start the prorate for the new plan based on the config' where name='change-plan-align-dates';
Update c_configuration set module='General',description='Api will check for the consitance and relations to Mater and Detail table' where name='constraint_approach_for_datatables';
Update c_configuration set module='CMS',description='In mobile we are checking for this flage and allowing the used to use the logins using multiple devices' where name='cuncerrent-sessions';
Update c_configuration set module='Order',description='In Post-paid we use this flag to decide to post the DC upon disconnection in between the contract' where name='disconnection-credit';
Update c_configuration set module='Order',description='on fifo basis the Device and Plan will be automatically associated with a value single' where name='implicit-association';
Update c_configuration set module='Master',description='When creating the Ip Pool we have to include the Network as well Broadcast ip in the range' where name='include-network-broadcast-ip';
Update c_configuration set module='PaymentGateway',description='used in the mobile to make paypal check out' where name='is-paypal';
Update c_configuration set module='Client',description='If this is enable the email as username and auto password is created for the client to use in selfcare, Mobile and Network(Stalker, PPPoe) ' where name='is-selfcareuser';
Update c_configuration set module='Client',description='If this is enabled then we have option to make payment then it will use that as storage of amount for On Demand services purchases' where name='is-wallet-enable';
Update c_configuration set module='Client',description='Used while captureing the client it will enable to capture the username and password' where name='login';
Update c_configuration set module='General',description='we use this in Mobile app to send notification to the given email id' where name='media-crash-email';
Update c_configuration set module='General',description='we use this in payment gateway posting or failuer notification to gvien email id ' where name='payment-email-description';
Update c_configuration set module='Provision',description='We use the provisioning system for the ad-hoc command via command center or job generated' where name='osd-provisioningSystem';
Update c_configuration set module='Selfcare',description='if this is enabled a defult plan detials given will be automatically book while user does a self registration using the selfcare registration process' where name='register-plan';
Update c_configuration set module='Selfcare',description='if this is configure while registration of selfcare for the first time we also capture the avgailable device using the serial no ' where name='registration-requires-device';
Update c_configuration set module='Payment Gateway',description='If this flage is enable we trigger the re-processing of the payment in case of failure based on the failure message and retry mechanism' where name='reProcess-interval';
Update c_configuration set module='Billing',description='As a generic configuration to round the decimal to given digits' where name='rounding';
Update c_configuration set module='General',description='Configure the email server details to send the emails' where name='smtp';
Update c_configuration set module='General',description='used to configure the sms url and rest of the strign will be prepared usign a db function ' where name='sms-configuration';
Update c_configuration set module='Order',description='While booking the order for fixed term when this is true then it will consider as Prorata and No of Periods else it considers any day periods'  where name='align-biling-cycle';
