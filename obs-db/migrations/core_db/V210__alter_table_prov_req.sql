
Drop procedure IF EXISTS removeprovreqcolumns;
DELIMITER //
create procedure removeprovreqcolumns() 
Begin
 
ALTER TABLE `b_provisioning_request` 
DROP COLUMN `response_status`,
DROP COLUMN `response_date`,
DROP COLUMN `response_message`,
DROP COLUMN `request_message`,
CHANGE COLUMN `status` `status` INT(1) NOT NULL ;
END //
DELIMITER ;
call removeprovreqcolumns();
Drop procedure IF EXISTS removeprovreqcolumns;



CREATE TABLE IF NOT EXISTS `b_provisioning_request_detail` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `provisioning_req_id` BIGINT(20) NOT NULL,
  `request_message` TEXT NOT NULL,
  `response_message` VARCHAR(500) NULL DEFAULT 'null',
  `response_date` DATETIME NULL DEFAULT NULL,
  `response_status` VARCHAR(45) NULL DEFAULT 'null',
  PRIMARY KEY (`id`),
  INDEX `fk_preq_id` (`provisioning_req_id` ASC),
  CONSTRAINT `fk_preq_id` FOREIGN KEY (`provisioning_req_id`)
    REFERENCES `b_provisioning_request` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


