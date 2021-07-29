CREATE TABLE IF NOT EXISTS `b_addons_service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plan_id`  int(10) NOT NULL,
  `charge_code` varchar(10) NOT NULL,
  `price_region_id` int(10) NOT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `unique_addser_plan_ch` (`plan_id`,`charge_code`)
) ;

CREATE TABLE IF NOT EXISTS `b_addons_service_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `adservice_id` int(11) NOT NULL,  -- b_addons_service.id
  `service_id` int(20) ,  -- refers b_service.id
  `price` decimal(19,6) NOT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
   PRIMARY KEY (`id`),
 CONSTRAINT `asp_FK1` FOREIGN KEY (`adservice_id`) REFERENCES `b_addons_service` (`id`)
);


CREATE TABLE IF NOT EXISTS `b_orders_addons` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(10) NOT NULL,
  `service_id` bigint(10) NOT NULL,
  `contract_id` bigint(10) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `status` varchar(10) DEFAULT NULL,
  `provision_system` varchar(10) DEFAULT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci';


insert ignore into m_permission values(null,'portfolio','CREATE_ADDONS','ADDONS','CREATE',0);
insert ignore into m_permission values(null,'portfolio','UPDATE_ADDONS','ADDONS','UPDATE',0);
insert ignore into m_permission values(null,'portfolio','DELETE_ADDONS','ADDONS','DELETE',0);
Drop procedure IF EXISTS serviceProvisioning; 
DELIMITER //
create procedure serviceProvisioning() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='provision_system'  
      and TABLE_NAME = 'b_prov_service_details'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_prov_service_details add column provision_system varchar(10) DEFAULT NULL after `sub_category`;

END IF;
END //
DELIMITER ;
call serviceProvisioning();
Drop procedure IF EXISTS serviceProvisioning;
insert ignore into m_permission values(null,'Client&orders','CREATE_ORDERADDONS','ORDERADDONS','CREATE',0);