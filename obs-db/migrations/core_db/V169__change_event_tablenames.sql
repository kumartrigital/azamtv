Drop procedure if exists changevodtablenames;
DELIMITER //
CREATE PROCEDURE changevodtablenames()
BEGIN
IF EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_event_master') then
IF NOT EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_mod_master') then
  SET @ddl = CONCAT('RENAME table b_event_master to b_mod_master');
  PREPARE STMT FROM @ddl;
  EXECUTE STMT;
END IF; 
END IF;
IF EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_event_detail') then
IF NOT EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_mod_detail') then
  SET @ddl = CONCAT('RENAME table b_event_detail to b_mod_detail');
  PREPARE STMT FROM @ddl;
  EXECUTE STMT;
END IF; 
END IF;
IF EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_event_pricing') then
IF NOT EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_mod_pricing') then
  SET @ddl = CONCAT('RENAME table b_event_pricing to b_mod_pricing');
  PREPARE STMT FROM @ddl;
  EXECUTE STMT;
END IF; 
END IF;
IF EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_event_pricediscount') then
IF NOT EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_mod_pricediscount') then
  SET @ddl = CONCAT('RENAME table b_event_pricediscount to b_mod_pricediscount');
  PREPARE STMT FROM @ddl;
  EXECUTE STMT;
END IF;
END IF; 
IF EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_eventorder') then
IF NOT EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_modorder') then
  SET @ddl = CONCAT('RENAME table b_eventorder to b_modorder');
  PREPARE STMT FROM @ddl;
  EXECUTE STMT;
END IF; 
END IF;
IF EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_eventorder_details') then
IF NOT EXISTS(Select * from INFORMATION_SCHEMA.TABLES where  TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_modorder_details') then
  SET @ddl = CONCAT('RENAME table b_eventorder_details to b_modorder_details');
  PREPARE STMT FROM @ddl;
  EXECUTE STMT;
END IF; 
END IF;
END //
DELIMITER ;
call changevodtablenames();
Drop procedure if exists changevodtablenames;

