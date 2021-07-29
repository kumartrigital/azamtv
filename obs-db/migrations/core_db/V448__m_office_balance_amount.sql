Drop procedure IF EXISTS officeamount;
DELIMITER //
create procedure officeamount() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'balance_amount'
     and TABLE_NAME = 'm_office_balance'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE m_office_balance
CHANGE COLUMN `balance_amount`  `balance_amount` DECIMAL(19,6) NOT NULL;
END IF;
END //
DELIMITER ;
call officeamount();
Drop procedure IF EXISTS officeamount;
