Drop procedure IF EXISTS teamMasters;
DELIMITER //
create procedure teamMasters() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'teamUserId'
     and TABLE_NAME = 'b_ticket_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_ticket_master`
ADD COLUMN `team_id` BIGINT(20) NULL AFTER `lastmodified_date`,
ADD COLUMN `team_user_id` BIGINT(20) NULL AFTER `team_id`,
ADD INDEX `fk_tm_team_id_idx` (`team_id` ASC),
ADD INDEX `fk_tm_user_id_idx` (`team_user_id` ASC);
ALTER TABLE `b_ticket_master`
ADD CONSTRAINT `fk_tm_team_id`
  FOREIGN KEY (`team_id`)
  REFERENCES `b_team` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_tm_user_id`
  FOREIGN KEY (`team_user_id`)
  REFERENCES `m_appuser` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
END IF;
END //
DELIMITER ;
call teamMasters();
Drop procedure IF EXISTS teamMasters;

Drop procedure IF EXISTS teamDetails;
DELIMITER //
create procedure teamDetails() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'teamUserId'
     and TABLE_NAME = 'b_ticket_details'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_ticket_details`
ADD COLUMN `team_id` BIGINT(20) NULL DEFAULT NULL AFTER `created_date`,
ADD COLUMN `team_user_id` BIGINT(20) NULL AFTER `team_id`;
END IF;
END //
DELIMITER ;
call teamDetails();
Drop procedure IF EXISTS teamDetails;
