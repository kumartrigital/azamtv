Drop procedure IF EXISTS adddistprospect;
DELIMITER //
create procedure adddistprospect() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'district'
     and TABLE_NAME = 'b_prospect'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_prospect` 
ADD COLUMN `district` varchar(100) NOT NULL ;
END IF;
END //
DELIMITER ;
call adddistprospect();
Drop procedure IF EXISTS adddistprospect;




Drop procedure IF EXISTS addservpoid;
DELIMITER //
create procedure addservpoid() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'client_service_poid'
     and TABLE_NAME = 'b_client_service'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_client_service` 
ADD COLUMN `client_service_poid` varchar(10) NULL ;
END IF;
END //
DELIMITER ;
call addservpoid();
Drop procedure IF EXISTS addservpoid;
