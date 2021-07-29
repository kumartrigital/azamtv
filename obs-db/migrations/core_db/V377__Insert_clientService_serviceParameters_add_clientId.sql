Drop procedure IF EXISTS officeClientId;
DELIMITER //
create procedure officeClientId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'client_id'
     and TABLE_NAME = 'm_office'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE m_office 
ADD COLUMN `client_id` BIGINT(20) NULL DEFAULT NULL AFTER `DAS_Type`;
END IF;
END //
DELIMITER ;
call officeClientId();
Drop procedure IF EXISTS officeClientId;


INSERT IGNORE INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`) VALUES ('isPartnerAgreement', '1', 'partnerAgreement', 'Office', 'it will create a client by using  office data');


INSERT IGNORE INTO `b_network_element` (`system_code`,`system_name`,`status`,`is_deleted`) 
values ('None','None','Active','N');
UPDATE `b_network_element` SET `id`='0' WHERE `system_code`='None';


INSERT IGNORE INTO `b_service_detail` (`service_id`, `param_type`, `param_name`, `param_value`, `param_category`, `is_deleted`)
values ('0', 'Combo', '192', 'select ne.id as id, ne.system_code as systemcode,ne.system_name as systemname from b_network_element ne where status=\'ACTIVE\'', 'S', 'N')

