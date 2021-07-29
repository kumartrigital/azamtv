

SET SQL_SAFE_UPDATES = 0;

UPDATE `stretchy_report` SET `report_sql`='select distinct cb.client_id AS clientId,bm.Due_date As dueDate\nfrom b_client_balance cb\njoin (select client_id,max(invoice_date) last_invoice_date,max(id) inv_id ,max(bill_id) bill_id\n            from b_bill_item where bill_id is not null group by client_id) bi\n            on (cb.client_id = bi.client_id and balance_amount >0)\njoin b_orders bo on (cb.client_id = bo.client_id and order_status = 1 and bo.is_deleted=\'n\' )\nleft join (select distinct bill_id,plan_code  from b_bill_details where plan_code is not null\n            and transaction_type !=\'ONETIME_CHARGES\') bd\n            on (bd.Bill_id =bi.bill_id)\nleft join  (select client_id,max(payment_date) last_payment_date ,max(id) pmt_id\n        from b_payments where is_deleted =0  group by client_id) bp\n            on (cb.client_id = bp.client_id)\nleft join b_bill_master bm on (bd.bill_id = bm.id and bm.is_deleted = \'N\' )\nwhere bo.plan_id = bd.plan_code AND bi.bill_id = bd.bill_id AND bm.Due_date = NOW() \norder by cb.client_id;' WHERE report_name='UnpaidCustomers';

SET SQL_SAFE_UPDATES = 1;
