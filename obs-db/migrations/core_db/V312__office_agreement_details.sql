Drop procedure IF EXISTS planAndContract;
DELIMITER //
create procedure planAndContract() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'plan_id'
     and TABLE_NAME = 'm_office_agreement_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_office_agreement_detail` 
ADD COLUMN `plan_id` BIGINT(30) NOT NULL ,
ADD COLUMN `contract_period` BIGINT(10) NOT NULL ,
ADD COLUMN `billing_frequency` VARCHAR(20) NOT NULL ;
END IF;
END //
DELIMITER ;
call planAndContract();
Drop procedure IF EXISTS planAndContract;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_CLIENTHARDWAREPLANACTIVATION', 'CLIENTHARDWAREPLANACTIVATION', 'CREATE', '0');



