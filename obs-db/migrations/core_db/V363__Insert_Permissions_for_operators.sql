/*
-- Query: select rp.permission_id from m_role r inner join m_role_permission rp on r.id = rp.role_id inner join m_permission p on  rp.permission_id = p.id where r.id=10
LIMIT 0, 1000

-- Date: 2018-08-04 14:14
*/

INSERT IGNORE INTO `m_role` VALUES (0,'OPERATOR','lco');
SET @r_rid=(select id from m_role where name='OPERATOR');

INSERT IGNORE INTO m_role_permission VALUES (@r_rid,78);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,110);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,111);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,113);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,277);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,305);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,337);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,340);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,343);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,355);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,357);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,388);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,396);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,414);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,422);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,424);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,425);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,427);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,430);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,431);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,443);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,447);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,460);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,461);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,466);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,473);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,475);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,566);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,578);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,592);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,595);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,641);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,645);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,646);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,714);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,794);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,828);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,853);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,996);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1001);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1002);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1003);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1013);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1014);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1015);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1016);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1039);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1047);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1049);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1053);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1059);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1063);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1068);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1070);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1073);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1093);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1094);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1095);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1096);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1098);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1099);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1100);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1101);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1120);
INSERT IGNORE INTO m_role_permission VALUES (@r_rid,1138);


