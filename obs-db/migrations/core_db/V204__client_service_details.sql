ALTER TABLE `b_client_service` 
CHANGE COLUMN `Service_type` `Service_id` INT(20) NOT NULL ,
ADD INDEX `fk_bcs_serviceId_idx` (`Service_id` ASC);
ALTER TABLE `b_client_service` 
ADD CONSTRAINT `fk_bcs_serviceId`
  FOREIGN KEY (`Service_id`)
  REFERENCES `b_service` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;


ALTER TABLE `b_service_parameters` 
CHANGE COLUMN `order_id` `order_id` BIGINT(20) NULL ,
CHANGE COLUMN `plan_name` `plan_name` VARCHAR(50) NULL ,
CHANGE COLUMN `service_id` `clientservice_id` BIGINT(20) NULL DEFAULT NULL ,
ADD INDEX `fk_b_service_parameters_csid` (`clientservice_id` ASC);
ALTER TABLE `b_service_parameters` 
ADD CONSTRAINT `fk_b_service_parameters_csid`
  FOREIGN KEY (`clientservice_id`)
  REFERENCES `b_client_service` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
