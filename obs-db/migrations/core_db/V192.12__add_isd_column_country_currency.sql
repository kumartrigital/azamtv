insert ignore into b_eventaction_mapping values(null, 'Order Booking', 'Notify Activation', 'workflow_events', 'N', 'N');

Drop procedure IF EXISTS countryISD; 
DELIMITER //
create procedure countryISD() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'country_ISD'
     and TABLE_NAME = 'b_country_currency'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_country_currency ADD COLUMN `country_ISD` VARCHAR(20) NULL  AFTER `conversion_rate` 
, ADD UNIQUE INDEX `country_ISD_UNIQUE` (`country_ISD` ASC) ;

END IF;
END //
DELIMITER ;
call countryISD();
Drop procedure IF EXISTS countryISD;