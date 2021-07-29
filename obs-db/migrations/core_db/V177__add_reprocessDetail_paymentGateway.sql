Drop procedure IF EXISTS  paymentGateWay; 
DELIMITER //
create procedure paymentGateWay() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='reprocess_detail'  
      and TABLE_NAME = 'b_paymentgateway'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_paymentgateway add column reprocess_detail varchar(500) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call paymentGateWay();
Drop procedure IF EXISTS paymentGateWay;

-- insert ignore into  c_configuration values (null,'reProcess-interval',1,'360');
insert ignore into  c_configuration values (null,'reProcess-interval',1,'6');


