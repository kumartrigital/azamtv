SET SQL_SAFE_UPDATES = 0;

Drop procedure IF EXISTS nationalIdDrop; 
DELIMITER //
create procedure nationalIdDrop() 
Begin
  IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'national_id'
     and TABLE_NAME = 'b_clientuser'
     and TABLE_SCHEMA = DATABASE())THEN 
UPDATE m_client t1
        INNER JOIN b_clientuser t2 
             ON t1.id = t2.client_id
SET t1.external_id = t2.national_id 
WHERE t2.national_id <> '' and  t2.national_id <> Null;
alter table b_clientuser drop column national_id; 
END IF;
END //
DELIMITER ;
call nationalIdDrop();
Drop procedure IF EXISTS nationalIdDrop;

SET SQL_SAFE_UPDATES = 1;

Drop procedure If Exists serviceSortByColumn;
DELIMITER //
create procedure serviceSortByColumn() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'sort_by'
     and TABLE_NAME = 'b_service'
     and TABLE_SCHEMA = DATABASE())THEN 
ALTER TABLE b_service DROP COLUMN sort_by;
END IF;
END //
DELIMITER ;
call serviceSortByColumn();
Drop procedure IF EXISTS serviceSortByColumn;


Drop procedure IF EXISTS serviceSortBy; 
DELIMITER //
create procedure serviceSortBy() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'b_prov_service_details' and COLUMN_NAME ='sort_by'
     and TABLE_SCHEMA = DATABASE())THEN
 ALTER TABLE b_prov_service_details ADD COLUMN sort_by int(5) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call serviceSortBy();
Drop procedure IF EXISTS serviceSortBy;

INSERT IGNORE INTO  m_permission VALUES(null,'billing', 'CREATE_LINKUPACCOUNT', 'LINKUPACCOUNT', 'CREATE', '0');


