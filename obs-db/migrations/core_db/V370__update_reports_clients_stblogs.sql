

UPDATE `stretchy_report` SET `report_sql`='select distinct c.account_no AS accountNo,c.display_name AS CustomerName,c.activation_date as \'Activation\',\nbid.serial_no as STB,bip.serial_no_2 as SC,bpr.request_type as actions,\n(select system_code from b_network_element where id = bia.provisioning_id) as Casname\nFROM b_item_attribute bia,m_client c JOIN m_office o ON o.id = c.office_id\njoin b_item_detail bid ON bid.client_id = c.id join b_item_pairing bip ON bid.serial_no = bip.serial_no_1\njoin b_provisioning_request bpr ON bpr.client_id = c.id join b_item_master im ON bid.item_master_id = im.id\nwhere im.item_class = 1'  WHERE report_name = 'STB Logs Report';







INSERT ignore INTO `m_permission` ( `grouping`, `code`, `entity_name`, `action_name`) VALUES ('CRM', 'POST_PRODUCTMAPPING', 'PRODUCTMAPPING', 'CREATE');

set @id =(select id from stretchy_report where report_name='Client Listing');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '1', 'Start Date');
INSERT IGNORE INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '2', 'End Date');
