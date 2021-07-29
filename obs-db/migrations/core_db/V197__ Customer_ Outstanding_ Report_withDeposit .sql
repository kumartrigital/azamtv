SET SQL_SAFE_UPDATES=0;

UPDATE stretchy_report SET report_sql='select 
    custinv.client_id as customerId,
    custinv.Name,
    custinv.office,
    custinv.invoiceAmount,
    bdr.depositAmount,
    (ifnull(adjust.debitAdjustment, 0) + ifnull(bdr.debitcustomerAmount, 0)) as debitAdjustment,
    (ifnull(adjust.creditAdjustment, 0) + ifnull(bdr.creditServiceAmount, 0)) as creditAdjustment,
    bdr.refundAmount,
    ifnull(cast(round(sum(pay.amount_paid), 2) as char),0) as collectionAmount,
    cast(round(ifnull(custinv.invoiceAmount,0) + ifnull(bdr.depositAmount, 0) + ifnull(adjust.debitAdjustment, 0) + ifnull(bdr.debitcustomerAmount, 0) - ifnull(sum(pay.amount_paid), 0) - ifnull(creditAdjustment, 0) - ifnull(bdr.refundAmount, 0) - ifnull(bdr.creditServiceAmount, 0))as char) as Balance
from
    ((select 
        cust.account_no as customerNo,
            cust.display_name as Name,
            cust.id as client_id,
            off.id as office_id,
            off.name as Office,
            cast(round(sum(inv.invoice_amount), 2) as char) as invoiceAmount
    from
        m_client cust join m_office off ON  cust.office_id = off.id
        left join b_invoice inv ON cust.id = inv.client_id
    group by cust.id
    Order by cust.id) custinv
    left outer join b_payments pay ON custinv.client_id = pay.client_id
    left outer join (SELECT 
        ADJ.CLIENT_ID,
            sum(ADJ.DBADJ) as debitAdjustment,
            sum(ADJ.CRADJ) as creditAdjustment
    FROM
        (SELECT 
        ba.client_id,
            ba.adjustment_type,
            SUM((CASE adjustment_type
                WHEN ''DEBIT'' THEN adjustment_amount
                ELSE 0
            END)) AS DBADJ,
            SUM((CASE adjustment_type
                WHEN ''CREDIT'' THEN adjustment_amount
                ELSE 0
            END)) AS CRADJ
    FROM
        b_adjustments ba
    GROUP BY ba.client_id , ba.adjustment_type) AS ADJ
    GROUP BY ADJ.CLIENT_ID) adjust ON custinv.client_id = adjust.client_id
    left outer join (SELECT 
        BDR.clientId AS clientId,
            TRUNCATE(sum(BDR.debitcustomerAmount),2) as debitcustomerAmount,
            TRUNCATE(sum(BDR.creditServiceAmount),2) as creditServiceAmount,
            TRUNCATE(sum(BDR.depositAmount),2) AS depositAmount,
            TRUNCATE(sum(BDR.creditAmount),2) AS refundAmount
    FROM
        (SELECT 
        client_id AS clientId,
            transaction_type AS transactionType,
            SUM((CASE description
                WHEN ''Deposit'' THEN debit_amount
                ELSE 0
            END)) AS depositAmount,
            SUM((CASE description
                WHEN ''Refund'' THEN credit_amount
                ELSE 0
            END)) AS creditAmount,
            SUM((CASE description
                WHEN ''Payment Towards Refund Entry'' THEN debit_amount
                ELSE 0
            END)) AS debitcustomerAmount,
            SUM((CASE description
                WHEN ''Refund Adjustment towards Service Balance'' THEN credit_amount
                ELSE 0
            END)) AS creditServiceAmount
    FROM
        b_deposit_refund
    where
        description in (''Deposit'' , ''Refund'', ''Refund Adjustment towards Service Balance'', ''Payment Towards Refund Entry'')
    GROUP BY client_id , transaction_type) AS BDR
    GROUP BY clientId) bdr ON bdr.clientId = custinv.client_id) where custinv.office_id=''${officeId}'' or -1 = ''${officeId}''
GROUP BY custinv.client_id
ORDER BY custinv.client_id' where report_name='Customer Outstanding Report';


DROP procedure IF EXISTS refundMode; 
DELIMITER //
CREATE procedure refundMode() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='refundmode_id'
     and TABLE_NAME ='b_deposit_refund'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_deposit_refund ADD COLUMN refundmode_id INT(20) DEFAULT NULL AFTER description;
END IF;
END //
DELIMITER ;
call refundMode();
DROP procedure IF EXISTS refundMode;


UPDATE stretchy_report SET report_sql='select 
      t.officeName as officeName,
      t.clientId as clientId,
      t.clientName as clientName,
      t.transactionDate as transactionDate,
      t.typeOfMode as typeOfMode,
      t.amountCollection as amountCollection
FROM
    ((select 
        mcv1.code_value as officeType,
            off.name as officeName,
            off.id as officeId,
            clnt.id as clientId,
            clnt.display_name as clientName,
            DATE_FORMAT(pay.payment_date, ''%Y-%m-%d'') as transactionDate,
            pay.paymode_id as modeId,
            mcv.code_value as typeOfMode,
            cast(TRUNCATE(sum(ifnull(pay.amount_paid, 0)), 2) as char) as amountCollection
    from
        m_office off
    join m_client clnt ON off.id = clnt.office_id
    join b_payments pay ON clnt.id = pay.client_id
    join m_code_value mcv ON mcv.id = pay.paymode_id
    Left join m_code_value mcv1 ON off.office_type = mcv1.id
    where
        off.id = clnt.office_id
    group by clientId , transactionDate , pay.paymode_id order by clientId) 
   union all 
  (select mcv1.code_value as officeType,
            off.name as officeName,
            off.id as officeId,
            clnt.id as clientId,
            clnt.display_name as clientName,
            DATE_FORMAT(dbr.transaction_date, ''%Y-%m-%d'') as transactionDate,
            dbr.refundmode_id as modeId,
            concat(''Refund Through -'',mcv.code_value ) as typeOfMode,
            cast(TRUNCATE(- sum(ifnull(dbr.debit_amount, 0)), 2) as char) as amountCollection
    from
        m_office off
    join m_client clnt ON off.id = clnt.office_id
    join b_deposit_refund dbr ON clnt.id = dbr.client_id
    join m_code_value mcv ON mcv.id = dbr.refundmode_id
    Left join m_code_value mcv1 ON off.office_type = mcv1.id
    where
        off.id = clnt.office_id
    group by clientId ,transactionDate,dbr.refundmode_id order by clientId)) t 
 where (t.officeId= ''${officeId}'' or -1 = ''${officeId}'') 
 and (t.modeId= ''${paymode_id}'' or -1 = ''${paymode_id}'') 
 and t.transactionDate  between ''${startDate}'' and ''${endDate}''
group by clientId ,transactionDate,typeOfMode order by clientId'
where report_name='Collection Date Wise Details';








