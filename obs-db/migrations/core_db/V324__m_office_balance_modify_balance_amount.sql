Drop procedure IF EXISTS balanceAmount;
DELIMITER //
create procedure balanceAmount() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'balance_amount'
     and TABLE_NAME = 'm_office_balance'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_office_balance  modify balance_amount decimal(19,2);
END IF;
END //
DELIMITER ;
call balanceAmount();
Drop procedure IF EXISTS balanceAmount;




UPDATE `obsplatform-tenants`.`tenants` SET `locale_name`='en' WHERE `id`='1';











