
Drop procedure IF EXISTS addServiceCategoryInService;
DELIMITER //
create procedure addServiceCategoryInService() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_category'
     and TABLE_NAME = 'b_service'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_service add column service_category char(1) NOT NULL after `status`;

END IF;
END //
DELIMITER ;
call addServiceCategoryInService();
Drop procedure IF EXISTS addServiceCategoryInService;



CREATE TABLE IF NOT EXISTS `b_service_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_id` int(20) NOT NULL,
  `param_type` varchar(15) NOT NULL,	
  `param_name` varchar(50) NOT NULL,
  `param_value` varchar(200) NOT NULL,
  `is_deleted` char(1) DEFAULT 'n',
  PRIMARY KEY (`id`),
  KEY `fk_servcdetail_serviceId` (`service_id`),
  CONSTRAINT `fk_servcdetail_serviceId` FOREIGN KEY (`service_id`) REFERENCES `b_service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT =1 DEFAULT CHARSET=latin1;

Drop procedure IF EXISTS addservicecategory;
DELIMITER //
create procedure addservicecategory() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_id'
     and TABLE_NAME = 'b_service_parameters'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table b_service_parameters add column service_id bigint(20);

END IF;
END //
DELIMITER ;
call addservicecategory();
Drop procedure IF EXISTS addservicecategory;

Create table IF NOT EXISTS b_client_service (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`client_id` bigint(20) NOT NULL,
`Service_type` varchar(15) NOT NULL,
`status` varchar(10) NOT NULL,
`createdby_id` bigint(20) DEFAULT NULL,
`created_date` datetime DEFAULT NULL,
`lastmodified_date` datetime DEFAULT NULL,
`lastmodifiedby_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert IGNORE into m_permission (grouping,code,entity_name,action_name) values ('portfolio','CREATE_CLIENTSERVICE','CLIENTSERVICE','CREATE');

