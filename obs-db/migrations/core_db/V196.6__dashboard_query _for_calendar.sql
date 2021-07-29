INSERT IGNORE INTO `stretchy_report` (`report_name`,`report_type`,`report_subtype`,`report_category`,`report_sql`,`description`,`core_report`,`use_report`) 
VALUES ('NetActiveCalendar','Table','','Client',
'select actual_date, 
concat(if ( Transaction_type =''RENEWAL BEFORE AUTOEXIPIRY'',''REN BEF AUTO EXP'',
if ( Transaction_type =''RENEWAL AFTER AUTOEXIPIRY'',''REN AFT AUTO EXP'' ,Transaction_type )), 
'' = '', Count(order_id) ) cnt 
from net_activedtls_vw 
group by actual_date, transaction_type
',
'NetActiveCalendar',1,1);


SET @id=(select id from stretchy_report where report_name='NetActiveCalendar');

INSERT IGNORE INTO `stretchy_report_parameter`(`report_id`,`parameter_id`,`report_parameter_name`)
VALUES (@id,1,'From Date');
