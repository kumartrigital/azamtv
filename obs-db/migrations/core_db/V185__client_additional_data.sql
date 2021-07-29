DROP TABLE IF EXISTS `additional_client_fields`;
CREATE TABLE `additional_client_fields` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`client_id` bigint(20) NOT NULL,
`job_title` varchar(20) DEFAULT NULL,
`gender_id` int(11) DEFAULT NULL,
`finance_id` varchar(10) DEFAULT NULL,
`uts_customer_id` varchar(10) DEFAULT NULL,
`date_of_birth` date DEFAULT NULL,
`nationality_id` int(11) DEFAULT NULL,
`age_group_id` int(11) DEFAULT NULL,
`id_type` int(10) DEFAULT NULL,
`id_number` varchar(10) DEFAULT NULL,
`prefere_lan_id` int(11) DEFAULT NULL,
`prefere_communication_id` int(11) DEFAULT NULL,
`remarks` text,
PRIMARY KEY (`id`),
 CONSTRAINT `FK_additional_client_fields_1` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
 CONSTRAINT `FK_m_client_gender_m_code_value` FOREIGN KEY (`gender_id`) REFERENCES `m_code_value` (`id`),
 CONSTRAINT `FK_m_client_nationality_m_code_value` FOREIGN KEY (`nationality_id`) REFERENCES `m_code_value` (`id`),
 CONSTRAINT `FK_m_client_agegroup_m_code_value` FOREIGN KEY (`age_group_id`) REFERENCES `m_code_value` (`id`),
 CONSTRAINT `FK_m_client_idtype_m_code_value` FOREIGN KEY (`id_type`) REFERENCES `m_code_value` (`id`),
 CONSTRAINT `FK_m_client_preferelan_m_code_value` FOREIGN KEY (`prefere_lan_id`) REFERENCES `m_code_value` (`id`),
 CONSTRAINT `FK_m_client_preferecommunication_m_code_value` FOREIGN KEY (`prefere_communication_id`) REFERENCES `m_code_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


Drop procedure IF EXISTS addtitleClient; 
DELIMITER //
create procedure addtitleClient() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='title'  
      and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_client add column title varchar(10) DEFAULT NULL after `id`;

END IF;
END //
DELIMITER ;
call addtitleClient();
Drop procedure IF EXISTS addtitleClient;


INSERT IGNORE INTO `m_code`(id,code_name,is_system_defined,code_description) 
VALUES(null,'nationality',0,'Define Customer Nationality');
SET @a_lid:=(select id from m_code where code_name='nationality');
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Indian',0);
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Europian',0);

INSERT IGNORE INTO `m_code`(id,code_name,is_system_defined,code_description) 
VALUES(null,'Prefer Language',0,'Define Customer Language');
SET @a_lid:=(select id from m_code where code_name='Prefer Language');
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'English',0);
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Urdu',0);


INSERT IGNORE INTO `m_code`(id,code_name,is_system_defined,code_description) 
VALUES(null,'prefer Communication',0,'Define Communication for customer to send bills');
SET @a_lid:=(select id from m_code where code_name='Prefer Communication');
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Email',0);
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'SMS',0);

INSERT IGNORE INTO `m_code`(id,code_name,is_system_defined,code_description) 
VALUES(null,'Age Group',0,'Define Customer Age Group');
SET @a_lid:=(select id from m_code where code_name='Age Group');
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Young',0);
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Middlage',0);


insert  ignore into `c_configuration`(`id`,`name`,`enabled`,`value`) values (null,'client-additional-data',0,'');
