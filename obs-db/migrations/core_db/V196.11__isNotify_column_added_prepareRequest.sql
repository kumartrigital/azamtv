Drop procedure IF EXISTS prepareRequestNofity;
DELIMITER //
create procedure prepareRequestNofity() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_prepare_request'
     and COLUMN_NAME ='is_notify' )THEN
ALTER TABLE b_prepare_request ADD COLUMN `is_notify` char(1) NOT NULL DEFAULT 'N' AFTER status ;
END IF;
END //
DELIMITER ;
call prepareRequestNofity();
Drop procedure IF EXISTS prepareRequestNofity;


INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) 
values ('PENDING_REQUEST','OBS Pending Request','Dear User, <br/><br/>','Provisioning Request is Pending State,<br/> ClientId: <clientId>,<br/> OrderId: <orderId>,<br/>
Reason: <exceptionReason><br/><br/>','Thanks','E');

SET @ID=(SELECT id FROM b_message_template where template_description='PENDING_REQUEST');
INSERT IGNORE INTO b_message_params values (null, @ID,'<clientId>',1),
(null, @ID,'<orderId>',2),(null, @ID,'<exceptionReason>',3);

INSERT IGNORE INTO c_configuration(name,enabled,value,module,description) VALUES('cubiware-configuration',0,
'{"url" : "abcd","accessToken" : "abcd"}','ProvisioningSystem','Cubiware Provosioning System');
