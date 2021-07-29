
INSERT IGNORE into `stretchy_report` values(null, 'List Of Users', 'Table', 'Report','Organization','Select au.id,au.username,au.firstname,au.email,off.external_id as org_code,off.name Org_name,aurs.roles
from m_appuser au , m_office off, (Select a.appuser_id,GROUP_CONCAT(b.name) AS roles
from m_appuser_role a, m_role b where a.role_id=b.id group by a.appuser_id) aurs
where au.office_id=off.id  and aurs.appuser_id=au.id','List Of Users', '1', '1');
