SET SQL_SAFE_UPDATES = 0;
Drop procedure IF EXISTS serviceSortBy; 
DELIMITER //
create procedure serviceSortBy() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'b_service' and COLUMN_NAME ='sort_by'
     and TABLE_SCHEMA = DATABASE())THEN
 ALTER TABLE b_service ADD COLUMN sort_by int(5) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call serviceSortBy();
Drop procedure IF EXISTS serviceSortBy;


Drop procedure IF EXISTS addAllocationTypeInAssociation; 
DELIMITER //
create procedure addAllocationTypeInAssociation() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'b_association' and COLUMN_NAME ='allocation_type'
     and TABLE_SCHEMA = DATABASE())THEN
 ALTER TABLE b_association ADD COLUMN allocation_type varchar(5) NOT NULL;
END IF;
END //
DELIMITER ;
call addAllocationTypeInAssociation();
Drop procedure IF EXISTS addAllocationTypeInAssociation;
