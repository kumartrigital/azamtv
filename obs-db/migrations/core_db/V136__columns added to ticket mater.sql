/*Alter table b_ticket_master add column lastmodifiedby_id bigint(20);
Alter table b_ticket_master add column lastmodified_date datetime;*/


Drop procedure IF EXISTS ticketmaster; 
DELIMITER //
create procedure ticketmaster() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'lastmodifiedby_id'
     and TABLE_NAME = 'b_ticket_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_ticket_master  add column lastmodifiedby_id bigint(20);
END IF;
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'lastmodified_date'
     and TABLE_NAME = 'b_ticket_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_ticket_master  add column lastmodified_date datetime;
END IF;
END //
DELIMITER ;
call ticketmaster();
Drop procedure IF EXISTS ticketmaster;


INSERT IGNORE INTO `m_permission`(grouping,code,entity_name,action_name,can_maker_checker) 
VALUES ('logistics','UPDATE_SUPPLIER','SUPPLIER','UPDATE',1);
