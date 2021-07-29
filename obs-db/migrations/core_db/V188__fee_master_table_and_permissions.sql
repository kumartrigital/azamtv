CREATE  TABLE IF NOT EXISTS `b_fee_master` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `fee_code` VARCHAR(10) NULL ,
  `fee_description` VARCHAR(100) NULL DEFAULT NULL ,
  `transaction_type` VARCHAR(100) NULL DEFAULT NULL ,
  `charge_code` VARCHAR(10) NULL DEFAULT NULL ,
  `default_fee_amount` DOUBLE(22,6) NULL ,
  `is_deleted` VARCHAR(1) NULL DEFAULT 'N' ,
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `fee_code` (`fee_code`)) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `b_fee_detail` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `fee_id` INT NOT NULL ,
  `region_id` VARCHAR(50) NOT NULL ,
  `amount` DECIMAL(22,6) NOT NULL ,
  `is_deleted` CHAR(1) NOT NULL DEFAULT 'N' ,
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `feeid_with_region_uniquekey` (`fee_id`, `region_id`) ,
  KEY `fk_feeid` (`fee_id`),
  CONSTRAINT `fk_fee_id` FOREIGN KEY (`fee_id`) REFERENCES `b_fee_master` (`id`) ON DELETE NO ACTION ON UPDATE 
NO ACTION) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT IGNORE INTO m_permission VALUES(null,'organisation','READ_FEEMASTER','FEEMASTER','READ',0);
INSERT IGNORE INTO m_permission VALUES(null,'organisation','CREATE_FEEMASTER','FEEMASTER','CREATE',0);
INSERT IGNORE INTO m_permission VALUES(null,'organisation','UPDATE_FEEMASTER','FEEMASTER','UPDATE',0);
INSERT IGNORE INTO m_permission VALUES(null,'organisation','DELETE_FEEMASTER','FEEMASTER','DELETE',0);

INSERT IGNORE INTO m_code VALUES (null,'Transaction Type',0,'Define Customer Transaction Type');
SET @id = (select id from m_code where code_name='Transaction Type');

INSERT IGNORE INTO m_code_value VALUES (null,@id,'Service Transfer',0);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Reconnection',1);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Late Payment',2);








