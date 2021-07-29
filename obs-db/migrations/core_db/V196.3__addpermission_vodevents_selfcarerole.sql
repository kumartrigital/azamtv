Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'READ_EVENTORDER';
Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'CREATE_EVENTORDER';

insert ignore into c_configuration values(null,'is-logout-cache',0,'','General','we use this for removing login session when we close the application');

