INSERT  IGNORE INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Channel Wise SubScribers', 'Table', '', 'BroadCaster', 'select distinct\n    b.brc_name as BROADCASTER,\n    c.channel_name as CHANNELNAME,\n  (select distinct count(o.client_id) from b_orders o\njoin m_client cl on cl.id=o.client_id where o.order_status=1 and  date_format(o.active_date, \'%Y-%m-%d\') between \'${startDate}\' and \'${endDate}\'\n) as COUNT\nfrom\n    b_orders o\n        join\n    b_order_line ol ON ol.order_id = o.id\n        join\n    b_product p ON p.id = ol.product_id\n        join\n    b_prd_ch_mapping pcm ON pcm.product_id = p.id\n        join\n    b_channel c ON c.id = pcm.channel_id\n        join\n    b_broadcaster b ON b.id = c.broadcaster_id\nwhere\n    o.order_status = 1 and  date_format(o.active_date, \'%Y-%m-%d\') between \'${startDate}\' and \'${endDate}\'\n', 'channel wise  subscribers list', '0', '1');




set @id =(select id from stretchy_report where report_name='Channel Wise SubScribers');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '1', 'Start Date');
INSERT IGNORE INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '2', 'End Date');





