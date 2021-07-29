Drop procedure IF EXISTS creditlimit;
DELIMITER //
create procedure creditlimit() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'credit_limit'
     and TABLE_NAME = 'm_office_balance'
     and TABLE_SCHEMA = DATABASE())THEN

ALTER TABLE `m_office_balance` 
ADD COLUMN `credit_limit` decimal(24,4) NULL ;
END IF;
END //
DELIMITER ;
call creditlimit();
Drop procedure IF EXISTS creditlimit;


insert ignore into m_permission values(null,'CRM','UPDATE_OFFICECREDITLIMIT','OFFICECREDITLIMIT','UPDATE',1);

