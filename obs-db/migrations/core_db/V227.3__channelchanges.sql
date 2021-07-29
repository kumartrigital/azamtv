INSERT IGNORE INTO `m_code` (`code_name`, `is_system_defined`, `code_description`) 
VALUES ('Channel Category', '0', 'defination of channel category');

set @id =(select id from m_code where code_name='Channel Category');


insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'Telugu News',0);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'Hindi',1);

insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'Telugu Entertainment',2);




