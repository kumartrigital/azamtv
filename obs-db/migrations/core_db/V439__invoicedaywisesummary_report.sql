

SET SQL_SAFE_UPDATES = 0;


UPDATE `stretchy_report` SET `report_sql`='\nSELECT\n             DATE_FORMAT(inv.invoice_date, \'%d-%m-%Y\') as `INVOICE DAY`,\n              cast(TRUNCATE(sum(inv.invoice_amount),2) as char charset utf8) as `INVOICE AMOUNT`\nFROM \n      m_office off\n      JOIN\n      m_client clnt ON off.id = clnt.office_id\n      JOIN\n      b_bill_item inv  ON clnt.id = inv.client_id\n       JOIN\n      b_charge charge ON inv.id = charge.billitem_id AND charge.client_id = inv.client_id\n      LEFT JOIN\n      b_charge_tax ctx ON charge.billitem_id = ctx.billitem_id \n where (off.id = \'${officeId}\' or -1 = \'${officeId}\')  \n AND inv.invoice_date between \'${startDate}\' and \'${endDate}\'\n GROUP BY inv.invoice_date' WHERE report_name='Invoice Day Wise Summary ';

SET SQL_SAFE_UPDATES = 1;
