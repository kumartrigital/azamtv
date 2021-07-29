
insert IGNORE into  stretchy_report values(null,'Access Profile Report', 'Table', 'Report','AccessProfile','select a.username as USERNAME,r.name as ROLENAME,p.code as PERMISSION from m_appuser a
join m_appuser_role ar on ar.appuser_id = a.id
join m_role r on ar.role_id = r.id
join m_role_permission mpr on mpr.role_id = r.id
join m_permission p on p.id = mpr.permission_id','Access Profile Report','1','1');


insert ignore into stretchy_report values(null, 'Leads Report', 'Table', 'Report','Leads','select u.USERNAME ,CONCAT(p.first_name,p.last_name) as NAME, p.street_area as AREA,p.address as CITY,p.status as STATUS
from b_prospect p join m_appuser u on p.createdby_id=u.id','Leads Report', '1', '1');




insert IGNORE into  stretchy_report values(null,'Channel Composition and Package', 'Table', 'Report','BroadCaster','select 
c.channel_name as "Channelname",
pr.product_description as "package",
if(prd.is_deleted = "N","added","removed") as "status",
prd.lastmodified_date as "Date"
from
 b_channel c,b_prd_ch_mapping prd
join b_product pr on pr.id=prd.product_id
where c.id=prd.channel_id','Channel Composition and Package','1','1');

