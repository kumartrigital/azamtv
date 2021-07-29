Drop procedure IF EXISTS duration;
DELIMITER //
create procedure duration() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'duration'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_plan_master 
ADD COLUMN `duration` INT(20) NULL;
END IF;
END //
DELIMITER ;
call duration();
Drop procedure IF EXISTS duration;


Drop procedure IF EXISTS addDurationForeignKey;
DELIMITER //
create procedure addDurationForeignKey() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema. KEY_COLUMN_USAGE
     WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_plan_master'
     and COLUMN_NAME ='duration' and CONSTRAINT_NAME = 'fk_b_plan_master_1')THEN
ALTER TABLE b_plan_master 
ADD INDEX `fk_b_plan_master_1_idx` (`duration`),
ADD CONSTRAINT `fk_b_plan_master_1`
  FOREIGN KEY (`duration`)
  REFERENCES b_contract_period (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
END IF;
END //
DELIMITER ;
call addDurationForeignKey();
Drop procedure IF EXISTS addDurationForeignKey;
