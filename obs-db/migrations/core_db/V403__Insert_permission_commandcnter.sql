SET @p_id=(select id from m_permission where code ='READ_COMMANDCENTER');

SET @r_id=(select id from m_role where name='BROADCASTER');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@p_id);

SET @r_id=(select id from m_role where name='OPERATOR');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@p_id);

SET @r_id=(select id from m_role where name='CSR');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@p_id);

SET @r_id=(select id from m_role where name='FINANCE');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@p_id);

SET @r_id=(select id from m_role where name='SALES');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@p_id);


SET @q_id=(select id from m_permission where code ='READ_FINGERPRINT');

SET @r_id=(select id from m_role where name='BROADCASTER');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@q_id);

SET @r_id=(select id from m_role where name='CSR');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@q_id);

SET @r_id=(select id from m_role where name='FINANCE');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@q_id);

SET @r_id=(select id from m_role where name='SALES');
INSERT IGNORE INTO m_role_permission VALUES (@r_id,@q_id);

