Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'DELETERECURRINGBILLING_PAYMENTGATEWAY';

Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'UPDATE_CLIENT';
