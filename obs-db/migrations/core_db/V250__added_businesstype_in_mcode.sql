

Drop procedure IF EXISTS renameline2column;
DELIMITER //
create procedure renameline2column() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'line_2'
     and TABLE_NAME = 'b_office_address'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_office_address` 
CHANGE COLUMN `line_2` `business_type` VARCHAR(200) NULL DEFAULT NULL ;


END IF;
END //
DELIMITER ;
call renameline2column();
Drop procedure IF EXISTS renameline2column;



insert ignore into m_code(code_name, is_system_defined, code_description) 
values ('Business Type',0,'defination of Business Type');

set @id =(select id from m_code where code_name='Business Type');


insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'Primary Points',0);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'Secondary',1);











