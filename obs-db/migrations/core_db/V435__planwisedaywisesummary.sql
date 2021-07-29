SET SQL_SAFE_UPDATES = 0;

UPDATE `stretchy_report` SET `report_sql`='SELECT \n    plnmst.plan_description AS `PLAN`,\n    DATE_FORMAT(invc.Invoice_date, \'%d-%m-%Y\') AS `INVOICE DATE`,\n    cast(sum(truncate(chrg.charge_amount, 2)) as char) `CHARGE AMOUNT`,\n    cast(sum(truncate(chrg.discount_amount, 2)) as char) `DISCOUNT AMOUNT`,\n    cast(sum(truncate(ifnull(tax.Tax_amount, 0), 2)) as char) `TAX AMOUNT`,\n    cast(sum(truncate(invc.invoice_amount, 2)) as char) `INVOICE AMOUNT`\nFROM\n    b_plan_master plnmst\n        JOIN\n    b_orders ord ON plnmst.id = ord.plan_id\n        JOIN\n    b_charge chrg ON chrg.order_id = ord.id\n        JOIN\n    b_bill_item invc ON invc.client_id = chrg.client_id AND invc.id = chrg.billitem_id AND charge_type = \'RC\'\n        JOIN\n    m_client clnt ON clnt.id = invc.client_id\n        LEFT OUTER JOIN\n    b_charge_tax tax ON tax.charge_id = chrg.id\nWHERE\n  (plnmst.id = \'${planId}\' or - 1 = \'${planId}\') and invc.Invoice_date between \'${startDate}\' and \'${endDate}\'\nGROUP BY `INVOICE DATE`,`PLAN` order by `INVOICE DATE`' WHERE report_name='Plan Wise Revenue DayWise Summary';


SET SQL_SAFE_UPDATES = 1;




