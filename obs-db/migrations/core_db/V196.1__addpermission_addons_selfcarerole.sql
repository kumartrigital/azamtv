Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'CREATE_ORDERADDONS';

Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'DISCONNECT_ORDERADDONS';

Insert ignore into m_permission values(null,'client&orders','READ_ADDONS','ADDONS','READ',0); 

Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'READ_ADDONS';
