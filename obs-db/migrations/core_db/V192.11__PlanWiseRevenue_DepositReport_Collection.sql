SET @offId=(SELECT id FROM stretchy_parameter where parameter_label='Office');
SET @ID=(SELECT id FROM stretchy_report where report_name='Paymode Collection Chart');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@ID,@offId,'Office');

-- Collection Month Wise Summary insert queiry

insert ignore into stretchy_report values(null, 'Collection Month Wise Summary', 'Table', '','Invoice&collections',
'select 
     off.name as office_Name,
     Year(pay.payment_date) AS Years,
     monthname(pay.payment_date) AS Months,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) as Amount_Collection
   
from
    m_office off
        join
    m_client clnt ON off.id = clnt.office_id
        join
    b_payments pay ON clnt.id = pay.client_id
 where (off.id = ''${officeId}'' or -1 = ''${officeId}'') 
   group by Months
order by Year(pay.payment_date)', 'Collection Month Wise Summary', '0', '1');

SET @id = (select id from stretchy_report where report_name='Collection Month Wise Summary');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@offId,'Office');

-- Collection Month Wise chart insert queiry

insert ignore into stretchy_report values(NULL, 'Collection Month Wise chart', 'Chart', 'Pie', 'Invoice&collections', 
'select 
     monthname(pay.payment_date) AS Months,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) as Amount_Collection
   
from
    m_office off
        join
    m_client clnt ON off.id = clnt.office_id
        join
    b_payments pay ON clnt.id = pay.client_id
     where (off.id = ''${officeId}'' or -1 = ''${officeId}'') and
 (pay.paymode_id = ''${paymode_id}'' or -1 =''${paymode_id}'' ) 
  and pay.payment_date between ''${startDate}'' and ''${endDate}''
group by Months
order by Year(pay.payment_date)', 'Collection Month Wise chart', '1', '1');

SET @id = (select id from stretchy_report where report_name='Collection Month Wise chart');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@offId,'Office');


-- Invoice Month Wise Summary insert queiry

insert ignore into stretchy_report values(null, 'Invoice Month Wise Summary', 'Table', '', 'Invoice&Collections', 'SELECT
            off.name as office_Name,
            Year(inv.invoice_date) AS Year,
             MONTHNAME(inv.invoice_date) as Month,
            cast(TRUNCATE(sum(inv.invoice_amount),2) as char charset utf8) as invoice_Amount

FROM 
      m_office off
      JOIN
      m_client clnt ON off.id = clnt.office_id
      JOIN
      b_invoice inv  ON clnt.id = inv.client_id
       JOIN
      b_charge charge ON inv.id = charge.invoice_id AND charge.client_id = inv.client_id
      LEFT JOIN
      b_charge_tax ctx ON charge.invoice_id = ctx.invoice_id 
   where (off.id = ''${officeId}'' or -1 = ''${officeId}'')  
  GROUP BY Month','Invoice Month Wise Summary','0', '1');

SET @id = (select id from stretchy_report where report_name='Invoice Month Wise Summary');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@offId,'Office');

-- Invoice Month Wise chart insert queiry

insert ignore into stretchy_report values(NULL,'Invoice Month Wise chart ','Chart','Pie','Invoice&Collections', 
'SELECT
             MONTHNAME(inv.invoice_date) as Month,
            cast(TRUNCATE(sum(inv.invoice_amount),2) as char charset utf8) as invoice_Amount

FROM 
      m_office off
      JOIN
      m_client clnt ON off.id = clnt.office_id
      JOIN
      b_invoice inv  ON clnt.id = inv.client_id
       JOIN
      b_charge charge ON inv.id = charge.invoice_id AND charge.client_id = inv.client_id
      LEFT JOIN
      b_charge_tax ctx ON charge.invoice_id = ctx.invoice_id 
where (off.id = ''${officeId}'' or -1 = ''${officeId}'') 
 GROUP BY Month','Invoice Month Wise chart ', '1', '1');

SET@id = (select id from stretchy_report where report_name='Invoice Month Wise chart');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@offId,'Office');



-- Invoice Month Wise chart insert queiry

insert ignore into stretchy_report values(NULL,'Invoice Month Wise chart ','Chart','Pie','Invoice&Collections', 
'SELECT
             MONTHNAME(inv.invoice_date) as Month,
            cast(TRUNCATE(sum(inv.invoice_amount),2) as char charset utf8) as invoice_Amount

FROM 
      m_office off
      JOIN
      m_client clnt ON off.id = clnt.office_id
      JOIN
      b_invoice inv  ON clnt.id = inv.client_id
       JOIN
      b_charge charge ON inv.id = charge.invoice_id AND charge.client_id = inv.client_id
      LEFT JOIN
      b_charge_tax ctx ON charge.invoice_id = ctx.invoice_id 
where (off.id = ''${officeId}'' or -1 = ''${officeId}'') 
 GROUP BY Month','Invoice Month Wise chart ', '1', '1');

SET@id = (select id from stretchy_report where report_name='Invoice Month Wise chart');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@offId,'Office');

SET SQL_SAFE_UPDATES=0;
Delete from stretchy_report where report_name='Collection_Day_wise_Details';
Delete from stretchy_report where report_name='Collection_Month_wise_Summary';
Delete from stretchy_report where report_name='Invoice_Datewise_Details';
Delete from stretchy_report where report_name='Invoice_Monthwise_Summary';

-- DROP VIEW IF EXISTS `bMaster_vw_SN`,`bDetails_vw_sn`,`bmaster_vw_sn`,`bdetails_vw_sn`;

-- Plan_Wise_Revenue_Detail --

SET FOREIGN_KEY_CHECKS=0;
insert ignore into stretchy_parameter values(null, 'planSelectAll', 'planId', 'Plan Name', 'select', 'number', '-1', NULL, NULL, 'Y', 'select id,plan_description as plan from b_plan_master where is_deleted=''n''',NULL,'Report');

insert ignore into stretchy_report values(Null, 'Plan_Wise_Revenue_Detail', 'Table', '','Invoice&Collections','SELECT plnmst.plan_description Plan_Name,
invc.id Invoice_no,DATE_FORMAT(invc.Invoice_date,''%Y-%m-%d'') as Invoice_date,chrg.client_id,clnt.display_name ClientName,
DATE_FORMAT(chrg.charge_start_date,''%Y-%m-%d'') as Charge_From,DATE_FORMAT(chrg.charge_end_date,''%Y-%m-%d'') as Charge_To,
cast(round(chrg.charge_amount,2) as char) charge_amount,cast(round(chrg.discount_amount,2)as char) discount_amount,
cast(round(ifnull(tax.Tax_amount,0),2) as char)  Tax_amount,cast(round(invc.invoice_amount,2) as char) invoice_amount 
FROM b_plan_master plnmst JOIN b_orders ord ON plnmst.id = ord.plan_id JOIN b_charge chrg ON chrg.order_id = ord.id 
JOIN b_invoice invc ON invc.client_id = chrg.client_id AND invc.id = chrg.invoice_id AND charge_type = ''RC'' 
JOIN m_client clnt ON clnt.id = invc.client_id LEFT OUTER JOIN b_charge_tax tax ON tax.charge_id = chrg.id 
WHERE  (plnmst.id = ''${planId}'' or -1 = ''${planId}'') and invc.Invoice_date between ''${startDate}'' and ''${endDate}''
GROUP BY plnmst.id, invc.id ORDER BY invc.id','Plan_Wise_Revenue_Detail','0','1');

UPDATE stretchy_report
SET report_sql='SELECT plnmst.plan_description Plan_Name,
invc.id Invoice_no,DATE_FORMAT(invc.Invoice_date,''%Y-%m-%d'') as Invoice_date,chrg.client_id,clnt.display_name ClientName,
DATE_FORMAT(chrg.charge_start_date,''%Y-%m-%d'') as Charge_From,DATE_FORMAT(chrg.charge_end_date,''%Y-%m-%d'') as Charge_To,
cast(round(chrg.charge_amount,2) as char) charge_amount,cast(round(chrg.discount_amount,2)as char) discount_amount,
cast(round(ifnull(tax.Tax_amount,0),2) as char)  Tax_amount,cast(round(invc.invoice_amount,2) as char) invoice_amount 
FROM b_plan_master plnmst JOIN b_orders ord ON plnmst.id = ord.plan_id JOIN b_charge chrg ON chrg.order_id = ord.id 
JOIN b_invoice invc ON invc.client_id = chrg.client_id AND invc.id = chrg.invoice_id AND charge_type = ''RC'' 
JOIN m_client clnt ON clnt.id = invc.client_id LEFT OUTER JOIN b_charge_tax tax ON tax.charge_id = chrg.id 
WHERE  (plnmst.id = ''${planId}'' or -1 = ''${planId}'') and invc.Invoice_date between ''${startDate}'' and ''${endDate}''
GROUP BY plnmst.id,invc.id ORDER BY invc.id'
where report_name='Plan_Wise_Revenue_Detail';

SET @id=(SELECT id FROM stretchy_report where report_name='Plan_Wise_Revenue_Detail');
SET @planID=(SELECT id FROM stretchy_parameter where parameter_label='Plan Name');
insert ignore into stretchy_report_parameter (report_id,parameter_id,report_parameter_name) values (@id,@planID,'Plan Name');
SET @DATEID=(SELECT id FROM stretchy_parameter where parameter_label='StartDate');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DATEID,'StartDate');
SET @DID=(SELECT id FROM stretchy_parameter where parameter_label='EndDate');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DID,'EndDate');

-- Plan_wise_Revenue_MonthWise --

insert ignore into stretchy_report values(Null, 'Plan_wise_Revenue_MonthWise', 'Table', '','Invoice&Collections','SELECT plnmst.plan_description Plan_Name,
Monthname(invc.Invoice_date) AS Month,
cast(round(sum(chrg.charge_amount),2)as char) ChargeAmount,
cast(round(sum(chrg.discount_amount),2)as char) Discount_Amount,
cast(round(sum(ifnull(tax.Tax_amount, 0)),2)as char) TaxAmount,
cast(round((sum(chrg.charge_amount)- sum(chrg.discount_amount)+ sum(ifnull(tax.Tax_amount, 0))),2) as char )AS invoice_amount
 FROM b_plan_master plnmst JOIN b_orders ord ON plnmst.id = ord.plan_id JOIN 
b_charge chrg ON chrg.order_id = ord.id JOIN b_invoice invc ON  invc.client_id = chrg.client_id 
AND invc.id = chrg.invoice_id AND charge_type = ''RC'' JOIN 
m_client clnt ON clnt.id = invc.client_id LEFT OUTER JOIN 
b_charge_tax tax ON tax.charge_id = chrg.id 
WHERE  (plnmst.id = ''${planId}'' or -1 = ''${planId}'')
GROUP BY Monthname(invc.Invoice_date), plnmst.id','Plan_wise_Revenue_MonthWise','0','1');

UPDATE stretchy_report
SET report_sql='SELECT plnmst.plan_description Plan_Name,
Monthname(invc.Invoice_date) AS Month,
cast(round(sum(chrg.charge_amount),2)as char) ChargeAmount,
cast(round(sum(chrg.discount_amount),2)as char) Discount_Amount,
cast(round(sum(ifnull(tax.Tax_amount, 0)),2)as char) TaxAmount,
cast(round((sum(chrg.charge_amount)- sum(chrg.discount_amount)+ sum(ifnull(tax.Tax_amount, 0))),2) as char )AS invoice_amount
 FROM b_plan_master plnmst JOIN b_orders ord ON plnmst.id = ord.plan_id JOIN 
b_charge chrg ON chrg.order_id = ord.id JOIN b_invoice invc ON  invc.client_id = chrg.client_id 
AND invc.id = chrg.invoice_id AND charge_type = ''RC'' JOIN 
m_client clnt ON clnt.id = invc.client_id LEFT OUTER JOIN 
b_charge_tax tax ON tax.charge_id = chrg.id 
WHERE  (plnmst.id = ''${planId}'' or -1 = ''${planId}'')
GROUP BY Monthname(invc.Invoice_date), plnmst.id'
where report_name='Plan_wise_Revenue_MonthWise';

SET @id=(SELECT id FROM stretchy_report where report_name='Plan_wise_Revenue_MonthWise');
SET @planID=(SELECT id FROM stretchy_parameter where parameter_label='Plan Name');
insert ignore into stretchy_report_parameter (report_id,parameter_id,report_parameter_name) values (@id,@planID,'Plan Name');

-- Deposit And Refund Date wise Details

insert ignore into stretchy_report values(Null, 'Deposite And Refund Date wise Details', 'Table', '','Invoice&Collections', 'select
     off.name as Office_Name,
     clnt.id as Client_Id,
     clnt.display_name as Client_Name,
     DATE_FORMAT(bdr.transaction_date,''%Y-%m-%d'') as Deposite_Date,
    TRUNCATE(ifnull( bdr.debit_amount, 0),2) as Deposit_Amount,
     DATE_FORMAT(pay.payment_date,''%Y-%m-%d'') as Collection_Date,
     TRUNCATE((pay.amount_paid),2) as Collection_Amount,
     DATE_FORMAT(bdr1.transaction_date,''%Y-%m-%d'')as Refund_Date,
     TRUNCATE(bdr1.credit_amount,2) as Refund_Amount            
  From
    m_office off
       join
    m_client clnt ON off.id = clnt.office_id
     join
    b_deposit_refund bdr ON bdr.client_id=clnt.id and bdr.description=''Deposit''
     left join 
    b_deposit_refund bdr1 ON bdr.client_id=clnt.id and bdr1.ref_id=bdr.id and bdr1.description=''Refund''
      left join
    b_payments pay ON clnt.id = pay.client_id and pay.id=bdr.payment_id 
  where (off.id = ''${officeId}'' or -1 = ''${officeId}'') and  bdr.transaction_date between ''${startDate}'' and ''${endDate}''
 group by clnt.id,bdr.id
 order by off.office_type,clnt.id', 'Deposite And Refund Date wise Details of Customers', '0', '1');

SET @id=(select id from stretchy_report where report_name='Deposite And Refund Date wise Details');
SET @offId=(SELECT id FROM stretchy_parameter where parameter_label='Office');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@offId,'Office');

SET @DATEID=(SELECT id FROM stretchy_parameter where parameter_label='StartDate');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DATEID,'StartDate');

SET @DID=(SELECT id FROM stretchy_parameter where parameter_label='EndDate');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DID,'EndDate');

    









