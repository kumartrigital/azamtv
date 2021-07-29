Drop procedure IF EXISTS paymentLongText; 
DELIMITER //
create procedure paymentLongText() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'Remarks'
     and TABLE_NAME = 'b_paymentgateway'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_paymentgateway CHANGE COLUMN `Remarks` `Remarks` LONGTEXT  CHARACTER SET utf8 DEFAULT NULL;
END IF;
END //
DELIMITER ;
call paymentLongText();
Drop procedure IF EXISTS paymentLongText;

Drop procedure IF EXISTS paymentGatewayReprocess; 
DELIMITER //
create procedure paymentGatewayReprocess() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'reprocess_detail'
     and TABLE_NAME = 'b_paymentgateway'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_paymentgateway ADD COLUMN reprocess_detail LONGTEXT  CHARACTER SET utf8 DEFAULT NULL;
END IF;
END //
DELIMITER ;
call paymentGatewayReprocess();
Drop procedure IF EXISTS paymentGatewayReprocess;

Drop procedure IF EXISTS configurationValue; 
DELIMITER //
create procedure configurationValue() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'value'
     and TABLE_NAME = 'c_configuration'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE c_configuration change COLUMN `value` `value` LONGTEXT  CHARACTER SET utf8 DEFAULT NULL;
END IF;
END //
DELIMITER ;
call configurationValue();
Drop procedure IF EXISTS configurationValue;

SET @id = (select id from m_code where code_name='Provisioning');
INSERT IGNORE INTO m_code_value VALUES(null,@id,'CubiWare',8);
INSERT IGNORE INTO m_code_value VALUES(null,@id,'CMS',9);

