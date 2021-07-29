INSERT IGNORE INTO `m_role` VALUES (0,'FINANCE','FINANCE');
SET @r_rid=(select id from m_role where name='FINANCE');


INSERT IGNORE INTO m_role_permission VALUES (@r_rid,78);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,110);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,325);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,340);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,341);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,342);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,343);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,358);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,359);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,422);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,425);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,649);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,667);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,669);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,813);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,814);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,824);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,828);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,829);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,831);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1059);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1141);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1143);
