INSERT IGNORE into `stretchy_report` values(null, 'list of Organization', 'Table', 'Report','Organization','Select a.external_id as code,a.Name,a.office_type,b.phone_number,(select name from m_office b 
where b.id=a.parent_id) parent_name 
from m_office a , b_office_address b where a.id=b.office_id','list of Organization / Entity ', '1', '1');

INSERT IGNORE into `stretchy_report` values(null, 'operators under organization', 'Table', 'Report','Organization','Select id,office_type,name,(Select count(0) from m_office b 
where b.hierarchy like concat(\".\",a.id,\'%\')) entity_count from m_office a','operators under organization', '1', '1');


