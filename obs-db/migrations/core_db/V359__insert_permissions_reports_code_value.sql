SET @a_pt:=(select id from m_code where code_name='plan Type');
INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@a_pt, 'Settlement', '2');

set sql_safe_updates = 0;
Update m_permission set grouping = 'Masters' where grouping in ('Master/Others','Master/Core');
Update m_permission set grouping = 'Inventory' where grouping in ('Device','logistics');
Delete from m_permission where grouping='PA';
Update m_permission set grouping = 'Customer' where grouping in ('portfolio');
Update m_permission set grouping = 'Check_Remove' where grouping in ('Remove');
Update m_permission set grouping = 'Masters' where grouping in ('celcom');
Update m_permission set grouping = 'Customer' where grouping in ('General');
Update m_permission set grouping = 'Service_Order' where grouping like 'Service%';
Update m_permission set grouping = 'Service_Order' where grouping like 'order%';
Update m_permission set grouping = 'System' where grouping like 'Batch%';
set sql_safe_updates = 1;


UPDATE `stretchy_report` SET `report_sql`='select distinct c.account_no AS accountNo, c.display_name AS CustomerName, c.activation_date as \"Activation\", bid.serial_no as STB, bip.serial_no_2 as SC, bpr.request_type as actions, bia.model AS model,(select code_value from m_code_value where id = bia.provisioning_id) as Casname FROM m_client c JOIN m_office o ON o.id = c.office_id join b_item_detail bid on bid.client_id = c.id join b_item_attribute bia on bid.item_model = bia.id\njoin b_item_pairing bip on bid.serial_no=bip.serial_no_1 join b_provisioning_request bpr on bpr.client_id=c.id join b_item_master im on bid.item_master_id=im.id where im.item_class=1' WHERE report_name = 'STB Logs Report';



UPDATE `stretchy_report` SET `report_sql` = 'select distinct date_format(c.activation_date ,\'%Y-%m-%d\') AS activation_month ,c.account_no ASaccountNo,c.display_name AS CustomerName,bid.serial_no as STB,bip.serial_no_2 as SC,bpr.request_type as actions,bia.model AS model,(select code_value from m_code_value where id = bia.provisioning_id) as Casname FROM m_client c JOIN m_office o ON o.id = c.office_id
join b_item_detail bid on bid.client_id = c.id join b_item_attribute bia on bid.item_model = bia.id join b_item_pairing bip on bid.serial_no=bip.serial_no_1 join b_provisioning_request bpr on bpr.client_id=c.id join b_item_master im on bid.item_master_id=im.id where im.item_class=1' WHERE report_name = 'STB Month Report';
