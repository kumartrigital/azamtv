INSERT IGNORE INTO `m_role` VALUES (0,'BROADCASTER','BROADCASTER');
SET @r_rid=(select id from m_role where name='BROADCASTER');

INSERT IGNORE INTO m_role_permission VALUES (@r_rid,305);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1093);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1094);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1095);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1098);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1099);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1100);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1101);
