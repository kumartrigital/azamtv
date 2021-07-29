
CREATE TABLE `obstenant-default`.`b_lease_details` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `office_id` BIGINT(15) NULL,
  `salutation` VARCHAR(45) NULL,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `mobile_number` VARCHAR(45) NULL,
  `nin_number` VARCHAR(45) NULL,
  `city` VARCHAR(45) NULL,
  `state` VARCHAR(45) NULL,
  `country` VARCHAR(45) NULL,
  `status` VARCHAR(45) NULL,
  `otp` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));

