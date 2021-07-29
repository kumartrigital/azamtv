INSERT IGNORE into `stretchy_report` values(null,'Master Package Report', 'Table', 'Report','BroadCaster','Select
pr.product_code as BOUQUET_CODE,pr.product_description as BUNDLE_NAME,c.channel_name as CHANNEL_NAME,m.ABV_ID,m.CISCO_ID,m.IRDETO_ID,m.GOSPEL_ID,p.price as PRICE from b_prd_ch_mapping cp,b_channel 
c,b_plan_pricing p,product_casid_vw m,b_product pr
where c.id=cp.channel_id and p.product_id=cp.product_id
and m.product_id=cp.product_id and pr.id=cp.product_id and upper(p.is_deleted)=''N''',' Master Package Report','1','1');


INSERT IGNORE into `stretchy_report` values(null, 'Master Channel Report', 'Table', 'Report','BroadCaster', 'Select c.channel_category as GROUP_NAME,c.channel_name as SERVICE_NAME,p.price as PRICE,m.ABV_ID,m.CISCO_ID,m.IRDETO_ID,m.GOSPEL_ID,b.brc_name as BROADCASTER_NAME from b_prd_ch_mapping cp,b_channel c,b_broadcaster b,b_plan_pricing p,product_casid_vw m,b_product pr
Where c.id=cp.channel_id and c.broadcaster_id=b.id and p.product_id=cp.product_id
and m.product_id=cp.product_id and pr.id=cp.product_id  and upper(p.is_deleted)=''N''', 'Master Channel Report', '1', '1');

INSERT IGNORE into `stretchy_report` values(null, 'Master Alacarte Report', 'Table', 'Report','BroadCaster',
'Select pr.product_code as BUNDLE_CODE ,c.channel_name as BUNDLE_NAME,pr.product_description as SERVICE_NAME,m.ABV_ID,m.CISCO_ID,m.IRDETO_ID,m.GOSPEL_ID from b_prd_ch_mapping cp,b_channel 
c,b_plan_pricing p,product_casid_vw m,b_product pr
join b_plan_detail pd on pd.product_id = pr.id
join b_sales_cataloge_mapping scm on scm.plan_id = pd.plan_id
join b_sales_cataloge sc on sc.id = scm.cataloge_id
join m_code_value mcv on mcv.id = sc.sales_plan_category_id
where c.id=cp.channel_id and p.product_id=cp.product_id
and m.product_id=cp.product_id and pr.id=cp.product_id and mcv.code_value =\'Alcarte Pack\' and upper(p.is_deleted)=''N''', 'Master Alacarte Report', '1', '1');


SET FOREIGN_KEY_CHECKS=0;
Drop view if exists product_casid_vw;
Create view product_casid_vw as
Select product_id,param_name,
GROUP_CONCAT(IF(param_name='196',param_value,NULL)) as ABV_ID,
GROUP_CONCAT(IF(param_name='238',param_value,NULL)) as CISCO_ID ,
GROUP_CONCAT(IF(param_name='241',param_value,NULL)) as IRDETO_ID,
GROUP_CONCAT(IF(param_name='239',param_value,NULL)) as GOSPEL_ID,
GROUP_CONCAT(IF(param_name='242',param_value,NULL)) as GOSPEL1_ID
from b_product_detail group by product_id;






