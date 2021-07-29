INSERT IGNORE into `stretchy_report` values(null, 'List Of Users', 'Table', 'Report','Organization','Select au.id,au.username,au.firstname,au.email,off.external_id as org_code,off.name Org_name,aurs.roles
from m_appuser au , m_office off, (Select a.appuser_id,GROUP_CONCAT(b.name) AS roles
from m_appuser_role a, m_role b where a.role_id=b.id group by a.appuser_id) aurs
where au.office_id=off.id  and aurs.appuser_id=au.id and off.hierarchy like \'${currentUserHierarchy}%\' ','List Of Users', '1', '1');


Insert IGNORE into  stretchy_report values(null,'Access Profile Report', 'Table', 'Report','AccessProfile','select a.username as USERNAME,r.name as ROLENAME,p.code as PERMISSION from m_appuser a
join m_office o on o.id = a.office_id
join m_appuser_role ar on ar.appuser_id = a.id
join m_role r on ar.role_id = r.id
join m_role_permission mpr on mpr.role_id = r.id
join m_permission p on p.id = mpr.permission_id where o.hierarchy like \'${currentUserHierarchy}%\'','Access Profile Report','1','1');


