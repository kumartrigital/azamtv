
set @id =(select id from m_code where code_name='Command');

insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'Fingerprint',0);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'OSD',3);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'BMail',4);
