Drop procedure IF EXISTS portfolioCommand; 
DELIMITER //
create procedure portfolioCommand() 
Begin
 IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'loan_id'
     and TABLE_NAME = 'm_portfolio_command_source'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_portfolio_command_source drop column loan_id;
END IF;

IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'savings_account_id'
     and TABLE_NAME = 'm_portfolio_command_source'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_portfolio_command_source drop column savings_account_id;
END IF;

IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'product_id'
     and TABLE_NAME = 'm_portfolio_command_source'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_portfolio_command_source drop column product_id;
END IF;

END //
DELIMITER ;
call portfolioCommand();
Drop procedure IF EXISTS portfolioCommand;  




Drop procedure IF EXISTS addserviceAutoColumn; 
DELIMITER //
create procedure addserviceAutoColumn() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_auto'
     and TABLE_NAME = 'b_service'
     and TABLE_SCHEMA = DATABASE())THEN
Alter  table b_service  add column is_auto char(1) DEFAULT 'N';
END IF;
END //
DELIMITER ;
call addserviceAutoColumn();
Drop procedure IF EXISTS addserviceAutoColumn;
