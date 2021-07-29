Drop procedure IF EXISTS clientBalanceServiceId;
DELIMITER //
create procedure clientBalanceServiceId()
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_id'
     and TABLE_NAME = 'b_client_balance'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_client_balance
ADD COLUMN `service_id` BIGINT(20)  NULL AFTER `wallet_amount`,
ADD COLUMN `resource_id` BIGINT(20)  NULL AFTER `service_id`,
ADD COLUMN `valid_from` DATETIME NULL DEFAULT NULL AFTER `resource_id`,
ADD COLUMN `valid_to` DATETIME NULL DEFAULT NULL AFTER `valid_from`,
ADD COLUMN `disputed_amount` DECIMAL(24,4) NULL DEFAULT NULL AFTER `valid_to`,
ADD COLUMN `reserved_amount` DECIMAL(24,4) NULL DEFAULT NULL AFTER `disputed_amount`,
ADD COLUMN `is_deleted` CHAR(1) NOT NULL DEFAULT 'N' AFTER `reserved_amount`;
END IF;
END //
DELIMITER ;
call clientBalanceServiceId();
Drop procedure IF EXISTS clientBalanceServiceId;


Drop procedure IF EXISTS currency;
DELIMITER //
create procedure currency() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currencyId'
     and TABLE_NAME = 'b_mod_pricing'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_mod_pricing`  
ADD COLUMN `currencyId` BIGINT(40) NOT NULL AFTER `price`;

END IF;
END //
DELIMITER ;
call currency();
Drop procedure IF EXISTS currency;









