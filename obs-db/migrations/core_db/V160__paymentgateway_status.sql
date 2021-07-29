INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values 
('PAYMENT_RECEIPT','Payment Confirmation','Dear <PARAM1><br/><br/>','Thank you for making your purchase for OBS.<br/><br/>
 This is a confirmation of your payment.<br/><br/> Result : <PARAM2>,<br/> Description : <PARAM3>,<br/>Amount : <PARAM4>,<br/>
 ReceiptNo : <PARAM5>.<br/>','Thankyou','E');

Drop procedure IF EXISTS paymentgatewayStatus; 
DELIMITER //
create procedure paymentgatewayStatus() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'status'
     and TABLE_NAME = 'b_paymentgateway'
     and TABLE_SCHEMA = DATABASE())THEN 
Alter table b_paymentgateway MODIFY status varchar(200);
END IF;
END //
DELIMITER ;
call paymentgatewayStatus();
Drop procedure IF EXISTS paymentgatewayStatus;
