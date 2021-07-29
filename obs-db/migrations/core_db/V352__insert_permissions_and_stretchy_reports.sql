INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Inventory', 'CREATE_ITEMDETAILS', 'ITEMDETAILS', 'CREATE', '1');

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`,`can_maker_checker`) VALUES ('Inventory', 'READ_ITEMDETAILS', 'ITEMDETAILS', 'READ',1);

UPDATE `stretchy_report` SET `report_sql`='select p.created_date as Date, u.USERNAME ,CONCAT(p.first_name,p.last_name) as NAME, p.street_area as AREA,p.address as CITY,p.status as STATUS\nfrom b_prospect p join m_appuser u on p.createdby_id=u.id' WHERE report_name = 'Leads Report';

set @id =(select id from stretchy_report where report_name='Leads Report');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '1', 'Start Date');







