INSERT IGNORE INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Monthly subscription for alacart channel', 'Table', '', 'BroadCaster', 'select  distinct\n    b.brc_name as BroadcasterName,\n    pm.plan_description as PackageName,\n    (select distinct count(o.client_id) from b_orders o\njoin m_client cl on cl.id=o.client_id where o.order_status=1 and  date_format(o.active_date, \'%Y-%m-%d\') between \'${startDate}\' and \'${endDate}\'\n) as SubscribersCount\nfrom\nb_orders o \n       join \n    b_order_line ol ON ol.order_id = o.id\n        join\n    b_product p ON p.id = ol.product_id\n        join\n    b_plan_detail pd ON pd.product_id = p.id\n        join\n    b_plan_master pm ON pm.id = pd.plan_id\n        join\n    b_sales_cataloge_mapping scm ON scm.plan_id = pd.plan_id\n        join \n	b_sales_cataloge sc ON sc.id = scm.cataloge_id\n        join\n    m_code_value mcv ON mcv.id = sc.sales_plan_category_id\n		join\n    b_prd_ch_mapping pcm ON pcm.product_id = p.id\n        join\n   b_channel c ON c.id = pcm.channel_id\n        join\n    b_broadcaster b ON b.id = c.broadcaster_id\nwhere\n    o.order_status = 1 and mcv.code_value = \'Alcarte Pack\' and  date_format(o.active_date, \'%Y-%m-%d\') between \'${startDate}\' and \'${endDate}\'\n', 'Monthly subscription for alacart channel', '0', '1');





set @id =(select id from stretchy_report where report_name='Monthly subscription for alacart channel');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '1', 'Start Date');
INSERT IGNORE INTO `stretchy_report_parameter` ( `report_id`, `parameter_id`, `report_parameter_name`) VALUES (@id, '2', 'End Date');

