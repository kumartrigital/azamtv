
Drop procedure IF EXISTS officeFunctional;
DELIMITER //
create procedure officeFunctional() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'office_id'
     and TABLE_NAME = 'b_prospect'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_prospect` 
ADD COLUMN `office_id` BIGINT(20) NOT NULL AFTER `prospect_type`,
ADD INDEX `fk_b_prospect_m_office_idx` (`office_id` ASC);
END IF;
END //
DELIMITER ;
call officeFunctional();
Drop procedure IF EXISTS officeFunctional;


Drop procedure IF EXISTS officeFunctional2;
DELIMITER //
create procedure officeFunctional2() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'office_id'
     and TABLE_NAME = 'b_prospect'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_prospect` 
ADD CONSTRAINT `fk_b_prospect_m_office`
  FOREIGN KEY (`office_id`)
  REFERENCES `m_office` (`id`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT;
END IF;
END //
DELIMITER ;
call officeFunctional2();
Drop procedure IF EXISTS officeFunctional2;

