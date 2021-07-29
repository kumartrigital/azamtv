insert ignore into r_enum_value values('order_actions',1,'ACTIVATION','ACTIVATION');
insert ignore into r_enum_value values('order_actions',2,'ACTIVATION','ACTIVATION');
insert ignore into r_enum_value values('order_actions',3,'CHANGE_PLAN','CHANGE_PLAN');
insert ignore into r_enum_value values('order_actions',4,'DISCONNECTION','DISCONNECTION');
insert ignore into r_enum_value values('order_actions',5,'REN AFT AUTO EXP','REN AFT AUTO EXP');
insert ignore into r_enum_value values('order_actions',6,'REN BEF AUTO EXP','REN BEF AUTO EXP');

insert ignore into `stretchy_parameter` values(null,'transactionTypeSelect','transactionType','transactionType','select','varchar','0',null,'Y',null,'select enum_id,enum_value from r_enum_value where enum_name ="order_actions";',null,'Single');


insert ignore into `stretchy_report` values (null,'ClientNotificationsByDay','Table','Table','Client','select  c.account_no ,c.display_name,v.plan from net_activedtls_vw v,m_client c WHERE v.client_id = c.id and v.transaction_type = ''${transactionType}''  AND date_format(v.actual_date,''%Y-%m-%d'')  = ''${startDate}''','Day Wise Net Activative Details',1,1);





SET @ID=(select id from stretchy_report where report_name='ClientNotificationsByDay');
SET @PID=(select id from stretchy_parameter where parameter_name='transactionTypeSelect');
SET @PSID=(select id from stretchy_parameter where parameter_name='startDateSelect');

insert ignore into stretchy_report_parameter  VALUES (null,@ID,@PID,'Transaction Type');
insert ignore into stretchy_report_parameter  VALUES (null,@ID,@PSID,'Start Date');
