UPDATE `stretchy_report` SET `report_sql`='select distinct concat(repeat(\"..\",   \n((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as \"Office/Branch\",\nc.id as \"Customer Id\",c.account_no as \"Client Account No.\",c.display_name as \"Customer Name\",c.external_id as \"External Id\",bid.serial_no as\"serialno\",bcs.status as status,bs.service_code as \"service\",casname,bia.model AS model,bia.item_type AS itemType\nfrom b_item_attribute bia, m_office o\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\njoin m_client c on c.office_id = ounder.id\njoin \n(Select b.client_id,b.id as clientservice_id,b.status ,(select z.code_value from \nm_code_value z where z.code_id=13 and z.id = c.parameter_value) as casname\nfrom  b_client_service b , b_service_parameters c\nwhere  b.id=c.clientservice_id ) x on c.id=x.client_id \njoin b_client_service bcs on bcs.client_id = c.id \njoin  \n(select id.client_id,id.serial_no,alc.clientservice_id  from  \nb_item_master im,b_item_detail id, b_allocation alc where im.id=id.item_master_id \nand im.item_class=1 and id.item_master_id=alc.item_master_id and id.client_id=alc.client_id) y on c.id=y.client_id and x.clientservice_id=y.clientservice_id  join b_item_detail bid on bid.client_id = c.id and  c.id=y.client_id JOIN b_service bs on bs.id = bcs.Service_id \nwhere o.id =${officeId} and bid.item_master_id = 1\norder by ounder.hierarchy, c.account_no' WHERE report_name = 'Client Listing';


insert IGNORE into  stretchy_report values(null,'STB Logs Report', 'Table', 'Report','Inventory','select distinct
    c.account_no AS accountNo,
    c.display_name AS CustomerName,
    c.activation_date as "Activation",
   bid.serial_no as STB,
   bip.serial_no_2 as SC,
   bpr.request_type as actions,
bia.model AS model,(select code_value from m_code_value where id = bia.provisioning_id) as Casname
FROM
  b_item_attribute bia,  m_client c 
        JOIN
    m_office o ON o.id = c.office_id
join b_item_detail bid on bid.client_id = c.id 
join b_item_pairing bip on bid.serial_no=bip.serial_no_1
join b_provisioning_request bpr on bpr.client_id=c.id join b_item_master im on bid.item_master_id
=im.id where im.item_class=1','STB Logs Report', '1', '1');



insert IGNORE into  stretchy_report values(null,'STB Month Report', 'Table', 'Report','Inventory','select distinct
date_format(c.activation_date ,\'%Y-%m-%d\') AS activation_month ,
    c.account_no AS accountNo,
    c.display_name AS CustomerName,
   bid.serial_no as STB,
   bip.serial_no_2 as SC,
   bpr.request_type as actions,
bia.model AS model,(select code_value from m_code_value where id = bia.provisioning_id) as Casname
FROM
  b_item_attribute bia,  m_client c
        JOIN
    m_office o ON o.id = c.office_id
join b_item_detail bid on bid.client_id = c.id
join b_item_pairing bip on bid.serial_no=bip.serial_no_1
join b_provisioning_request bpr on bpr.client_id=c.id join b_item_master im on bid.item_master_id
=im.id where im.item_class=1','STB month Report', '1', '1');

set @id =(select id from stretchy_report where report_name='STB Month Report');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '1', 'Start Date');
INSERT IGNORE INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '2', 'End Date');


