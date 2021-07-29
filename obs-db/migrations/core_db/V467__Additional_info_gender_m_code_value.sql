SET @a_lid:=(select id from m_code where code_name='Gender');
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Male',1);
INSERT IGNORE INTO `m_code_value`(id,code_id,code_value,order_position) VALUES(null,@a_lid,'Female',2);
