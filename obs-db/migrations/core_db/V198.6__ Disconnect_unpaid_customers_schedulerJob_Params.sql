
SET SQL_SAFE_UPDATES=0;

-- Invoicing Job --

UPDATE stretchy_report SET report_sql='select distinct  a.id as clientid from m_client a,b_orders b,b_order_price op where a.id =b.client_id and b.order_status = 1 and b.is_deleted=''n'' and b.id=op.order_id and (op.next_billable_day is null or op.next_billable_day <=now())' WHERE report_name='Invoicing';

-- Statement Job --

UPDATE stretchy_report SET report_sql='select b.client_id as clientId  from b_invoice b,m_client m,b_charge c where date(invoice_date)<=date(now()) and m.status_enum !=400  and m.id=b.client_id and c.invoice_id=b.id  and c.bill_id is null and b.bill_id is null group by clientId
UNION 
select p.client_id as clientId  from b_payments p,m_client m  where date(payment_date)<=date(now()) and m.status_enum !=400  and m.id=p.client_id and p.bill_id is null group by clientId
UNION 
select a.client_id as clientId  from b_adjustments a,m_client m  where date(adjustment_date)<=date(now()) and m.status_enum !=400  and m.id=a.client_id and a.bill_id is null group by clientId' WHERE report_name='Statement';

-- Make PDF Job --

UPDATE stretchy_report SET report_sql='select distinct id as billId from b_bill_master where filename=''invoice'' 
and is_deleted=''N'' and Due_date=now()' WHERE report_name='PDF Statement';

-- Unpaid Customers Job --

UPDATE stretchy_report SET report_sql='select distinct cb.client_id AS clientId,bm.Due_date As dueDate
from b_client_balance cb
join (select client_id,max(invoice_date) last_invoice_date,max(id) inv_id ,max(bill_id) bill_id
            from b_invoice where bill_id is not null group by client_id) bi
            on (cb.client_id = bi.client_id and balance_amount >0)
join b_orders bo on (cb.client_id = bo.client_id and order_status = 1 and is_deleted=''n'' )
left join (select distinct bill_id,plan_code  from b_bill_details where plan_code is not null
            and transaction_type !=''ONETIME_CHARGES'') bd
            on (bd.bill_id =bi.bill_id)
left join  (select client_id,max(payment_date) last_payment_date ,max(id) pmt_id
        from b_payments where is_deleted =0  group by client_id) bp
            on (cb.client_id = bp.client_id)
left join b_bill_master bm on (bd.bill_id = bm.id and bm.is_deleted = ''N'' )
where bo.plan_id = bd.plan_code AND bi.bill_id = bd.bill_id AND bm.Due_date = NOW() 
order by cb.client_id' WHERE report_name='UnpaidCustomers';


SET @ID=(select id from job where name='DISCONNECT_UNPAID_CUSTOMERS');
INSERT  IGNORE INTO job_parameters VALUES(null,@ID, 'dueDate', 'DATE', 'NOW()', Date_format(now(), '%d %M %Y'), 'Y', NULL);

UPDATE job SET cron_expression='0 0 22 L * ?',cron_description='End of Month Last Day Night' WHERE name='STATEMENT';
UPDATE job SET cron_expression='0 0 23 L * ?',cron_description='End of Month Last Day Night' WHERE name='PDF';

