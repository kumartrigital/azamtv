
 CREATE TABLE if not exists `b_provisioning_actions` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `provision_type` varchar(50) NOT NULL,
  `action` varchar(50) NOT NULL,
  `provisioning_system` varchar(20) DEFAULT NULL,
  `is_enable` char(1) DEFAULT 'N',
  `is_delete` char(1) DEFAULT 'N',
    PRIMARY KEY (`id`),
  UNIQUE KEY `provisining_key` (`provision_type`)
);

insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (1,'Create Client','CLIENT ACTIVATION','CMS','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (2,'Close Client','TERMINATE','CMS','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (3,'Event Order','PROVISION IT','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (4,'Release Device','RELEASE DEVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (5,'Create Agent','CREATE AGENT','CMS','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (6,'Create Nas','CREATE NAS','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (7,'Create RadSevice','CREATE RADSERVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (9,'Change Credentials','CHANGE CREDENTIALS','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (10,'Update RadService','UPDATE RADSERVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (11,'Remove RadService','REMOVE RADSERVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (12,'Update RadSevice','UPDATE RADSERVICE','Radius','N','N');
insert ignore into `b_provisioning_actions`(`id`,`provision_type`,`action`,`provisioning_system`,`is_enable`,`is_delete`) values (13,'Remove RadSevice','REMOVE RADSERVICE','Radius','N','N');

Drop procedure IF EXISTS makercheker; 
DELIMITER //
create procedure makercheker() 
Begin
IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'm_portfolio_command_source' and COLUMN_NAME ='command_as_json' and IS_NULLABLE='NO'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE m_portfolio_command_source CHANGE COLUMN `command_as_json` `command_as_json` TEXT DEFAULT NULL;
END IF;
END //
DELIMITER ;
call makercheker();
Drop procedure IF EXISTS makercheker;

insert ignore into b_provisioning_actions values(null,'Create Client','CLIENT ACTIVATION','Beenius','Y','N');
insert ignore into b_provisioning_actions values (null,'Close Client','TERMINATE','Beenius','Y','N');
insert ignore into b_provisioning_actions values (null,'Event Order','PROVISION IT','Beenius','Y','N');
insert ignore into b_provisioning_actions values (null,'Release Device','RELEASE DEVICE','Beenius','Y','N');

-- insert ignore into b_eventaction_mapping  VALUES (null,'Create Live Event','Active Live Event','workflow_events','N','Y');
insert ignore into m_permission values(null,'billing','ACTIVE_PROVISIONACTIONS','PROVISIONACTIONS','ACTIVE',0);


