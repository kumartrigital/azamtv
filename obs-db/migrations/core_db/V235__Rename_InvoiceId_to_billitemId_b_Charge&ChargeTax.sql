Drop procedure IF EXISTS removeInvoiceIdColumn;
DELIMITER //
create procedure removeInvoiceIdColumn() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'invoice_id'
     and TABLE_NAME = 'b_charge'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_charge` DROP COLUMN `invoice_id`;

END IF;
END //
DELIMITER ;
call removeInvoiceIdColumn();
Drop procedure IF EXISTS removeInvoiceIdColumn;


Drop procedure IF EXISTS addbillitemIdColumn;
DELIMITER //
create procedure addbillitemIdColumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'billitem_id'
     and TABLE_NAME = 'b_charge'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_charge add column `billitem_id` INT(20) NOT NULL AFTER `client_id`;

END IF;
END //
DELIMITER ;
call addbillitemIdColumn();
Drop procedure IF EXISTS addbillitemIdColumn;




Drop procedure IF EXISTS removeInvoiceIdColumn;
DELIMITER //
create procedure removeInvoiceIdColumn() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'invoice_id'
     and TABLE_NAME = 'b_charge_tax'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_charge_tax` DROP COLUMN `invoice_id`;

END IF;
END //
DELIMITER ;
call removeInvoiceIdColumn();
Drop procedure IF EXISTS removeInvoiceIdColumn;


Drop procedure IF EXISTS addbillitemIdColumn;
DELIMITER //
create procedure addbillitemIdColumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'billitem_id'
     and TABLE_NAME = 'b_charge_tax'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_charge_tax add column `billitem_id` INT(20) NOT NULL AFTER `charge_id`;

END IF;
END //
DELIMITER ;
call addbillitemIdColumn();
Drop procedure IF EXISTS addbillitemIdColumn;
