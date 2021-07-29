set @id =(select id from stretchy_report where report_name='Package wise Count');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '1', 'Start Date');
INSERT IGNORE INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '2', 'End Date');
