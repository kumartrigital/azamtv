
set @id =(select id from stretchy_report where report_name='List Of Users');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '5', 'Office');





set @id =(select id from stretchy_report where report_name='operators under organization');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '5', 'Office');



set @id =(select id from stretchy_report where report_name='Access Profile Report');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '5', 'Office');


INSERT IGNORE into `stretchy_report` values(null, 'All Stocks Report', 'Table', 'Report','Inventory','select item.serial_no as STBNO,(select serial_no_2 from b_item_pairing where serial_no_1=item.serial_no)as SCNO,item.quality as SC_QUALITY,(select account_no from m_client where id = client_id) as CUSTOMER_NBR,office.external_id as ORGCODE,office.name as MCR_CODE,(select city from b_office_address where office_id=office.id) as LCO_CITY,(select city from b_office_address where office_id=office.id) as LCO_DISTRICT,(select city from b_office_address where office_id=office.id) as LCO_STATE,if(item.status = "Available","In Stack","In Use") as CONTRACT_STATUS FROM  b_item_detail item LEFT JOIN m_office office ON item.office_id = office.id LEFT JOIN b_grn g ON g.id = item.grn_id LEFT JOIN b_supplier s ON s.id = g.supplier_id LEFT JOIN b_item_master master ON item.item_master_id = master.id where master.item_class=1 and office.hierarchy like\'${currentUserHierarchy}%\'','All Stocks Report', '1', '1');


