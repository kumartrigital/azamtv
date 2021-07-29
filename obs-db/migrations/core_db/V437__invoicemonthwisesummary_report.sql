
SET SQL_SAFE_UPDATES = 0;

UPDATE `stretchy_report` SET `report_sql`='SELECT\n            off.name as BRANCH,\n            Year(inv.invoice_date) AS YEAR,\n             MONTHNAME(inv.invoice_date) as MONTH,\n            cast(TRUNCATE(sum(inv.invoice_amount),2) as char charset utf8) as AMOUNT\n\nFROM \n      m_office off\n      JOIN\n      m_client clnt ON off.id = clnt.office_id\n      JOIN\n      b_bill_item inv  ON clnt.id = inv.client_id\n       JOIN\n      b_charge charge ON inv.id = charge.billitem_id AND charge.client_id = inv.client_id\n      LEFT JOIN\n      b_charge_tax ctx ON charge.billitem_id = ctx.billitem_id \n   where (off.id = \'${officeId}\' or -1 = \'${officeId}\')  \n  GROUP BY MONTH' WHERE report_name='Invoice Month Wise Summary ';

SET SQL_SAFE_UPDATES = 1;
