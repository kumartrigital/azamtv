
set @id =(select id from m_code where code_name='office type');


DELETE FROM `m_code_value` WHERE `code_id`= @id AND code_value='Agent';

DELETE FROM `m_code_value` WHERE `code_id`= @id  AND code_value='HO';

DELETE FROM `m_code_value` WHERE `code_id`= @id  AND code_value='Office';

INSERT ignore INTO m_code_value (code_id, code_value, order_position) value(@id,'MSO',0);






