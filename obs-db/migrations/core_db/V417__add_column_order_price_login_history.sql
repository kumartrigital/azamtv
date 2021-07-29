Drop procedure IF EXISTS authToken;
DELIMITER //
create procedure authToken() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'auth_token'
     and TABLE_NAME = 'b_login_history'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_login_history 
ADD COLUMN `auth_token` TEXT NULL DEFAULT NULL AFTER `session_lastupdate`;
END IF;
END //
DELIMITER ;
call authToken();
Drop procedure IF EXISTS authToken;


Drop procedure IF EXISTS orderPriceCurrencyId;
DELIMITER //
create procedure orderPriceCurrencyId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency_id'
     and TABLE_NAME = 'b_order_price'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_order_price
ADD COLUMN `currency_id` BIGINT(20) NOT NULL AFTER `lastmodifiedby_id`;
END IF;
END //
DELIMITER ;
call orderPriceCurrencyId();
Drop procedure IF EXISTS orderPriceCurrencyId;
