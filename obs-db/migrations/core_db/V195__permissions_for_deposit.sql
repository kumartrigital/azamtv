insert ignore  into `m_permission`(`id`,`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`) values (null,'client&orders','CREATE_DEPOSIT','DEPOSIT','CREATE',0);

insert ignore into c_paymentgateway_conf (id,name,enabled,value,description) VALUES (null,'evo',0,'{\"url\":\"https://spg.evopayments.eu/pay/payssl.aspx\",\"merchantId\":\"pg_57966\"}',"Evo"); 

Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'CREATE_REDEMPTION';

Insert ignore into m_role_permission 
Select (Select id from m_role where name='selfcare') as rid,b.id from m_permission b where b.code = 'READ_VOUCHER';
