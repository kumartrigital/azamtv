INSERT ignore INTO `job` (`name`, `display_name`, `cron_expression`, `cron_description`,`create_time`, `task_priority`, `job_key`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`, `user_id`) VALUES ('ESCALATION', 'Ticket Escalation', '0 0/1 * 1/1 * ? *', 'Every One Minute',now(), '5', 'ESCALATIONJobDetaildefault _ DEFAULT', '0', '0', '1', '0', '0', '1');

set @id =(select id from job where name='ESCALATION');
INSERT ignore INTO `job_parameters` (`job_id`, `param_name`, `param_type`, `param_value`, `is_dynamic`) VALUES (@id, 'reportName', 'COMBO', 'Ticket Escalation', 'Y');


INSERT ignore INTO `stretchy_report` (`report_name`, `report_type`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Ticket Escalation', 'Table', 'Ticket', 'SELECT  ot.id as ticketId,ot.ticket_no as ticketNo, ot.status as ticketStatus,ot.ticket_date as createdDate,ot.is_escalated as escalations, um.manager_name as managerName, um.manager_email as managerEmail FROM b_office_ticket ot join b_user_manager um on ot.assigned_to = um.user_id WHERE ticket_date <= DATE_SUB(NOW(), INTERVAL 2 HOUR)', 'Tickets Escalation', '0', '1');



CREATE TABLE if not exists`b_user_manager` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `manager_name` varchar(50) NOT NULL,
  `manager_email` varchar(150) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;



INSERT ignore INTO `b_message_template` (`template_description`, `subject`, `header`, `body`, `footer`, `message_type`, `is_deleted`) VALUES ('NOTIFY_TICKET_ESCALATION', 'Notify Ticket Escalation', 'Dear <ManagerName>', 'The TicketNo : <ticketNo> created on <createdDateTime> has not been resolved in its included time, Please resolve it ', 'Thanking you', 'E', 'N');

