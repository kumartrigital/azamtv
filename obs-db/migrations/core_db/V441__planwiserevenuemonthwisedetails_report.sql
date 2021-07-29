SET SQL_SAFE_UPDATES = 0;

UPDATE `stretchy_report` SET `report_sql`='SELECT \n    plnmst.plan_description AS PLAN,\n    Monthname(invc.Invoice_date) AS MONTH,\n    YEAR(invc.Invoice_date) AS YEAR,\n    cast(round(sum(chrg.charge_amount), 2) as char) `CHARGE AMOUNT`,\n    cast(round(sum(chrg.discount_amount), 2) as char) `DISCOUNT AMOUNT`,\n    cast(round(sum(ifnull(tax.Tax_amount, 0)), 2) as char) `TAX AMOUNT`,\n    cast(round((sum(chrg.charge_amount) - sum(chrg.discount_amount) + sum(ifnull(tax.Tax_amount, 0))),2)\n        as char) AS `INVOICE AMOUNT`\nFROM\n    b_plan_master plnmst\n        JOIN\n    b_orders ord ON plnmst.id = ord.plan_id\n        JOIN\n    b_charge chrg ON chrg.order_id = ord.id\n        JOIN\n    b_bill_item invc ON invc.client_id = chrg.client_id AND invc.id = chrg.billitem_id AND charge_type = \'RC\'\n        JOIN\n    m_client clnt ON clnt.id = invc.client_id\n        LEFT OUTER JOIN\n    b_charge_tax tax ON tax.charge_id = chrg.id\n WHERE\n    (plnmst.id = \'${planId}\' or - 1 = \'${planId}\')\nGROUP BY Monthname(invc.Invoice_date), plnmst.id' WHERE report_name='Plan Wise Revenue MonthWise Details';

SET SQL_SAFE_UPDATES = 1;

