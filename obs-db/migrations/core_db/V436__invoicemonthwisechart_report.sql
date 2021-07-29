SET SQL_SAFE_UPDATES = 0;

UPDATE `stretchy_report` SET `report_sql`='SELECT\n             MONTHNAME(inv.invoice_date) as Month,\n            cast(TRUNCATE(sum(inv.invoice_amount),2) as char charset utf8) as invoice_Amount\n\nFROM \n      m_office off\n      JOIN\n      m_client clnt ON off.id = clnt.office_id\n      JOIN\n      b_bill_item inv  ON clnt.id = inv.client_id\n       JOIN\n      b_charge charge ON inv.id = charge.billitem_id AND charge.client_id = inv.client_id\n      LEFT JOIN\n      b_charge_tax ctx ON charge.billitem_id = ctx.billitem_id \nwhere (off.id = \'${officeId}\' or -1 = \'${officeId}\') \n GROUP BY Month' WHERE report_name='Invoice Month Wise chart ';


SET SQL_SAFE_UPDATES = 1;
