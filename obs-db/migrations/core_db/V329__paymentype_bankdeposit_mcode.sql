set @id =(select id from m_code where code_name='Payment Mode');

INSERT ignore into m_code_value(code_id, code_value, order_position) VALUES (@id, 'Bank Deposit', 6);






