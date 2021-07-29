
SET SQL_SAFE_UPDATES = 0;

UPDATE `obstenant-default`.`stretchy_report` SET `report_sql`='SELECT \n    concat(cast(DATE_FORMAT(invc.Invoice_date, \'%Y-%m-%d\') as char),\' - \',cast(plnmst.plan_description as char )) as Date,\n    cast(SUM(invc.invoice_amount) as char)\nFROM\n    b_plan_master plnmst\n        JOIN\n    b_orders ord ON plnmst.id = ord.plan_id\n        JOIN\n    b_charge chrg ON chrg.order_id = ord.id\n        JOIN\n    b_bill_item invc ON invc.client_id = chrg.client_id AND invc.id = chrg.billitem_id AND charge_type = \'RC\'\n        JOIN\n    m_client clnt ON clnt.id = invc.client_id\n        LEFT OUTER JOIN\n    b_charge_tax tax ON tax.charge_id = chrg.id\nWHERE\n  (plnmst.id = \'${planId}\' or - 1 = \'${planId}\') and invc.Invoice_date between \'${startDate}\' and \'${endDate}\'\nGROUP BY invc.Invoice_date,plnmst.plan_description\norder by invc.Invoice_date' WHERE report_name='Plan Wise Revenue DayWise chart';


SET SQL_SAFE_UPDATES = 1;
