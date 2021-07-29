DROP PROCEDURE IF EXISTS addingIsAggregateColumn;
DELIMITER //
CREATE PROCEDURE addingIsAggregateColumn() 
BEGIN
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_aggregate'
     and TABLE_NAME = 'b_charge_codes'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_charge_codes ADD COLUMN `is_aggregate` TINYINT(1) NOT NULL DEFAULT 0;
END IF;
END //
DELIMITER ;
CALL addingIsAggregateColumn();
DROP PROCEDURE IF EXISTS addingIsAggregateColumn;