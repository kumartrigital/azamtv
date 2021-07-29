INSERT IGNORE INTO `stretchy_report` (`report_name`,`report_type`,`report_subtype`,`report_category`,`report_sql`,`description`,`core_report`,`use_report`) 
VALUES ('ClientSchedulerOrders','Table','','Client',
'SELECT 
    ea.trans_date AS transactionDate,
    ea.event_action AS event,
    (CASE
        WHEN
            (ea.event_action = ''CREATE''
                and ea.entity_name = ''ORDER'')
        THEN
            concat(ea.action_name,
                    '' '',
                    ''Orders = '',
                     count(ea.id))
        ELSE NULL
    END) AS cnt
FROM
    m_office mo
        JOIN
    m_client c ON mo.id = c.office_id
        JOIN
    b_event_actions ea ON (ea.client_id = c.id
        and is_processed = ''N'')
GROUP BY ea.trans_date,ea.event_action',
'ClientSchedulerOrders',1,1);

INSERT IGNORE INTO `stretchy_report` (`report_name`,`report_type`,`report_subtype`,`report_category`,`report_sql`,`description`,`core_report`,`use_report`) 
VALUES ('ClientSchedulerOrdersOnDay','Table','','Client',
'SELECT 
    c.account_no as accountNo,c.id as clientId,
    c.display_name AS displayName
FROM
    m_office mo
        JOIN
    m_client c ON mo.id = c.office_id
        JOIN
    b_event_actions ea ON (ea.client_id = c.id
        and is_processed = ''N'')
WHERE  ea.event_action = ''CREATE'' AND  date_format(ea.trans_date,''%Y-%m-%d'')  = ''${startDate}''',
'ClientSchedulerOrdersOnDay',1,1);


SET @id=(select id from stretchy_report where report_name='ClientSchedulerOrdersOnDay');

INSERT IGNORE INTO `stretchy_report_parameter`(`report_id`,`parameter_id`,`report_parameter_name`)
VALUES (@id,1,'startDate');
