Drop procedure IF EXISTS addIsDeletedTickets;
DELIMITER //
create procedure addIsDeletedTickets() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_delete'
     and TABLE_NAME = 'm_document'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_document add column is_delete varchar(4) DEFAULT 'N';

END IF;
END //
DELIMITER ;
call addIsDeletedTickets();
Drop procedure IF EXISTS addIsDeletedTickets;

INSERT IGNORE INTO b_eventaction_mapping Values(null,'Add Comment','Send Email','workflow_events','N','N');

INSERT IGNORE INTO b_eventaction_mapping Values(null,'Close Ticket','Send Email','workflow_events','N','N');
