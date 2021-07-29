 CREATE TABLE IF NOT EXISTS `r_rum` (
  `id` INT(20) NOT NULL AUTO_INCREMENT,
  `charge_code_Id` INT(20) NOT NULL,
  `template_id` INT(20) NOT NULL,
  `rum_name` VARCHAR(45) NULL DEFAULT NULL,
  `rum_expression` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))ENGINE=InnoDB DEFAULT CHARSET=latin1;


 CREATE TABLE IF NOT EXISTS `r_uom` (
  `id` INT(20) NOT NULL AUTO_INCREMENT,
  `uom_name` VARCHAR(45) NULL,
  `uom_description` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert ignore into m_permission values(null,'collectionbatch','CREATE_RATABLEUSAGEMETRIC','RATABLEUSAGEMETRIC','CREATE',0);
insert ignore into m_permission values(null,'collectionbatch','UPDATE_RATABLEUSAGEMETRIC','RATABLEUSAGEMETRIC','UPDATE',0);


insert ignore into m_permission values(null,'collectionbatch','CREATE_UNITOFMEASUREMENT','UNITOFMEASUREMENT','CREATE',0);
insert ignore into m_permission values(null,'collectionbatch','UPDATE_UNITOFMEASUREMENT','UNITOFMEASUREMENT','UPDATE',0);
