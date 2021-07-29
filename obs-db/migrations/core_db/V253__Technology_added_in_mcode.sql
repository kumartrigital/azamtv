insert ignore into m_code(code_name, is_system_defined, code_description) 
values ('Technology',0,'defination of Technology');

set @id =(select id from m_code where code_name='Technology');

insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'Java',0);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'.Net',1);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'PHP',2);

set @id =(select id from m_code where code_name='SP');

insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'Username',9);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'Password',10);



