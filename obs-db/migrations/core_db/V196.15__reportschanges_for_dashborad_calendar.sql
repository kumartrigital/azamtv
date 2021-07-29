SET @id=(select id from stretchy_report where report_name='opTicketsbyDays');
update stretchy_report set 
    report_sql = 'select count(0) tkt_cnt,  date(ticket_date) days from b_ticket_master tm  LEFT JOIN m_client c on (c.id = tm.client_id and tm.`status`=''OPEN'')  LEFT JOIN m_office of on (of.id = c.office_id ) group by days'
 where id = @id;


SET @id=(select id from stretchy_report where report_name='ClientNotificationsByDay');
update stretchy_report set 
    report_sql = 'select  c.account_no ,c.id as clientId,c.display_name,v.plan from net_activedtls_vw v,m_client c WHERE v.client_id = c.id and v.transaction_type = ''${transactionType}''  AND date_format(v.actual_date,''%Y-%m-%d'')  = ''${startDate}'''
 where id = @id;


CREATE OR REPLACE
VIEW `client_tkt_dtls_vw` AS
    select 
        `o`.`name` AS `BRANCH`,
        `c`.`id` AS `CLIENT ID`,
        `c`.`display_name` AS `CLIENT NAME`,
		`tm`.`id` AS `TICKET ID`,
        date_format(`tm`.`ticket_date`, '%d-%m-%Y') AS `TICKET DATE`,
        `tm`.`description` AS `DESCRIPTION`,
        `tm`.`status` AS `STATUS`,
        date_format(`tm`.`closed_date`, '%d-%m-%Y') AS `CLOSED DATE`,
        time_format(timediff(`tm`.`closed_date`, `tm`.`ticket_date`),
                '%H:%i:%s') AS `RESOL TIME`,
        `td`.`comments` AS `COMMENTS`,
        `u2`.`username` AS `ASSIGNED TO`,
        `u`.`username` AS `CREATED USER`
    from
        (((((`b_ticket_details` `td`
        left join `m_appuser` `u` ON ((`td`.`createdby_id` = `u`.`id`)))
        left join `m_appuser` `u2` ON ((`td`.`assigned_to` = `u2`.`id`)))
        left join `b_ticket_master` `tm` ON ((`td`.`ticket_id` = `tm`.`id`)))
        left join `m_client` `c` ON ((`tm`.`client_id` = `c`.`id`)))
        left join `m_office` `o` ON ((`o`.`id` = `c`.`office_id`)))
    order by `tm`.`ticket_date` desc;
