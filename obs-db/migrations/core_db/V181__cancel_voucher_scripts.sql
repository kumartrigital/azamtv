insert ignore into m_code  VALUES (null,'Voucher Cancel Reason',0,'Reasons for Cancel Vouchers');
SET @id = (select id from m_code where code_name='Voucher Cancel Reason');

insert ignore into m_code_value VALUES (null,@id,'Mistake',0);

INSERT IGNORE INTO m_permission VALUES(null, 'organisation', 'UPDATE_RADSERVICE', 'RADSERVICE', 'UPDATE', '0');
INSERT IGNORE INTO m_permission VALUES(null, 'organisation', 'DELETE_RADSERVICE', 'RADSERVICE', 'DELETE', '0');
INSERT IGNORE INTO m_permission VALUES(null, 'organisation', 'CANCEL_VOUCHER', 'VOUCHER', 'CANCEL', '0');

INSERT IGNORE INTO b_provisioning_actions VALUES(null, 'Update RadSevice', 'UPDATE RADSERVICE', 'Radius', 'Y', 'N');
INSERT IGNORE INTO b_provisioning_actions VALUES(null, 'Remove RadSevice', 'REMOVE RADSERVICE', 'Radius', 'Y', 'N');


Drop procedure IF EXISTS addcancelfieldinpindetails; 
DELIMITER //
create procedure addcancelfieldinpindetails() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'cancel_reason'
     and TABLE_NAME = 'b_pin_details'
     and TABLE_SCHEMA = DATABASE())THEN
Alter  table b_pin_details add column cancel_reason varchar(50) DEFAULT null;
END IF;
END //
DELIMITER ;
call addcancelfieldinpindetails();
Drop procedure IF EXISTS addcancelfieldinpindetails; 
