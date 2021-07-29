Drop procedure IF EXISTS officeTicketId;
DELIMITER //
create procedure officeTicketId() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'id'
     and TABLE_NAME = 'b_office_ticket'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_office_ticket
CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT;
END IF;
END //
DELIMITER ;
call officeTicketId();
Drop procedure IF EXISTS officeTicketId;



Drop procedure IF EXISTS ticketDate;
DELIMITER //
create procedure ticketDate() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'ticket_date'
     and TABLE_NAME = 'b_office_ticket'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_office_ticket 
ADD COLUMN `ticket_date` DATETIME NULL DEFAULT NULL AFTER `problem_code`;
END IF;
END //
DELIMITER ;
call ticketDate();
Drop procedure IF EXISTS ticketDate;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES('Masters', 'READ_OFFICEADJUSTMENT', 'OFFICEADJUSTMENT', 'READ', '1');


