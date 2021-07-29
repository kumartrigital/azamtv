

INSERT IGNORE INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('STBTOTALCOUNT', 'Table', '', 'Inventory', 'select distinct\n(select distinct count(id.serial_no) from b_item_detail id, b_item_pairing ip join m_office b \nwhere id.office_id=b.id and id.serial_no = ip.serial_no_1  and b.hierarchy like concat(o.hierarchy,\'%\') )as TOTAL ,\n(select distinct count(cs.client_id) from b_client_service cs,m_office b join m_client c\nwhere cs.client_id = c.id and b.id=c.office_id and cs.status=\'ACTIVE\' and b.hierarchy like concat(o.hierarchy,\'%\') ) as ACTIVE,\n(select distinct count(cs.client_id) from b_client_service cs,m_office b join m_client c\nwhere cs.client_id = c.id and b.id=c.office_id and cs.status=\'TERMINATED\' and b.hierarchy like concat(o.hierarchy,\'%\') ) as DEACTIVE from b_item_detail id join m_office o on o.id=id.office_id where o.id=\'${officeId}\'\n', 'STBTOTALCOUNT', '0', '1');


set @id =(select id from stretchy_report where report_name='STBTOTALCOUNT');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '5', 'Office');


