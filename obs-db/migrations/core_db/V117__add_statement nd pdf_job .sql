INSERT  IGNORE INTO stretchy_report VALUES(null, 'Statement', 'Table', '', 'Scheduling Job', '(select b.client_id as clientId  from b_invoice b,m_client m  where date(invoice_date)<=date(now()) and m.status_enum !=400  and m.id=b.client_id and b.bill_id is null group by clientId)
UNION 
(select p.client_id as clientId  from b_payments p,m_client m  where date(payment_date)<=date(now()) and m.status_enum !=400  and m.id=p.client_id and p.bill_id is null group by clientId)
UNION 
(select a.client_id as clientId  from b_adjustments a,m_client m  where date(adjustment_date)<=date(now()) and m.status_enum !=400  and m.id=a.client_id and a.bill_id is null group by clientId)', '', '0', '0');

INSERT  IGNORE INTO stretchy_report VALUES(null, 'PDF Statement', 'Table', '', 'Scheduling Job', 'select id as billId from b_bill_master where filename=''invoice'';', '', '0', '0');


INSERT  IGNORE INTO job VALUES(null,'Generate Statement', 'Generate Statement', '0 0 12 1/1 * ? *', 'Daily once at Midnight', NOW(), '5', NULL, NOW(), NOW(), 'Generate StatementJobDetaildefault _ DEFAULT', NULL, '1', '0', '1', '0', '0', '1');
select @a_lid:=last_insert_id();
INSERT  IGNORE INTO job_parameters VALUES(null, @a_lid, 'dueDate', 'DATE', 'NOW()', '14 November 2013', 'Y', NULL);
INSERT  IGNORE INTO job_parameters VALUES(null, @a_lid, 'reportName', 'COMBO', NULL, 'Statement', 'Y', NULL);


INSERT  IGNORE INTO job VALUES(null, 'Generate PDF', 'Generate PDF', '0 0 12 1/1 * ? *', 'Daily once at Midnight', NOW(), '5', NULL, NOW(), NOW(), 'Generate PDFJobDetaildefault _ DEFAULT', NULL, '1', '0', '1', '0', '0', '1');
select @a_lid:=last_insert_id();
INSERT  IGNORE INTO job_parameters VALUES(null, @a_lid, 'processDate', 'DATE', 'NOW()', '14 November 2013', 'Y', NULL);
INSERT  IGNORE INTO job_parameters VALUES(null, @a_lid, 'reportName', 'COMBO', NULL, 'PDF Statement', 'Y', NULL);
INSERT  IGNORE INTO job_parameters VALUES(null, @a_lid, 'promotionalMessage', 'String', NULL, '', NULL, NULL);

