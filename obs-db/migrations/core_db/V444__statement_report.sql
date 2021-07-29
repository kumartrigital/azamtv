
SET SQL_SAFE_UPDATES = 0;

UPDATE `stretchy_report` SET `report_sql`='select b.client_id as clientId  from b_bill_item b,m_client m,b_charge c where date(invoice_date)<=date(now()) and m.status_enum !=400  and m.id=b.client_id and c.billitem_id=b.id  and c.bill_id is null and b.bill_id is null group by clientId\nUNION \nselect p.client_id as clientId  from b_payments p,m_client m  where date(payment_date)<=date(now()) and m.status_enum !=400  and m.id=p.client_id and p.bill_id is null group by clientId\nUNION \nselect a.client_id as clientId  from b_adjustments a,m_client m  where date(adjustment_date)<=date(now()) and m.status_enum !=400  and m.id=a.client_id and a.bill_id is null group by clientId\n' WHERE report_name='Statement';

SET SQL_SAFE_UPDATES = 1;
