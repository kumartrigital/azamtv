Drop procedure IF EXISTS removeOrderIdColumn;
DELIMITER //
create procedure removeOrderIdColumn() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'order_id'
     and TABLE_NAME = 'b_provisioning_request'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_provisioning_request` DROP COLUMN `order_id`;

END IF;
END //
DELIMITER ;
call removeOrderIdColumn();
Drop procedure IF EXISTS removeOrderIdColumn;


Drop procedure IF EXISTS addClientserviceIdColumn;
DELIMITER //
create procedure addClientserviceIdColumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'clientservice_id'
     and TABLE_NAME = 'b_provisioning_request'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_provisioning_request add column `clientservice_id` BIGINT(20) default NULL AFTER `client_id`;

END IF;
END //
DELIMITER ;
call addClientserviceIdColumn();
Drop procedure IF EXISTS addCodeModuleColumn;


RENAME TABLE b_invoice TO  b_bill_item;
SET SQL_SAFE_UPDATES=0;
UPDATE job SET name='CHARGE', display_name='Charging' WHERE name='INVOICING';
UPDATE job SET name='BILLING', display_name='Billing' WHERE name='STATEMENT';
UPDATE job SET name='INVOICE', display_name='Invoicing' WHERE name='PADF';
SET SQL_SAFE_UPDATES=1;


