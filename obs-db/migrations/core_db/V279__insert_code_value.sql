SET @m_mid=(select id from m_code where code_name='Ticket Status');
INSERT IGNORE INTO `m_code_value` (id,code_id,code_value,order_position) VALUES (null,@m_mid,'OPEN',1);
INSERT IGNORE INTO `m_code_value` (id,code_id,code_value,order_position) VALUES (null,@m_mid,'CLOSED',2);

