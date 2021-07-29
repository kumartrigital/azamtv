INSERT  ignore INTO m_code (code_name, is_system_defined,code_description)

VALUES ('Preference', '0','defination of Preferences');

set @id =(select id from m_code where code_name='Preference');

INSERT ignore INTO m_code_value (code_id, code_value, order_position) value(@id,'Email',0);
INSERT ignore INTO m_code_value (code_id, code_value, order_position) value(@id,'Phone Number',1);
INSERT ignore INTO m_code_value (code_id, code_value, order_position) value(@id,'Both',2);


