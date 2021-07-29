SET SQL_SAFE_UPDATES=0;

CREATE OR REPLACE VIEW `net_activedtls_vw` AS
    select 
        `dt`.`year4` AS `year4`,
        `dt`.`year_month_abbreviation` AS `year_mon`,
        `dt`.`month_number` AS `month_number`,
        `fdm`.`date_value` AS `fdm`,
        `ldm`.`date_value` AS `ldm`,
        `o`.`client_id` AS `client_id`,
        `o`.`id` AS `order_id`,
        `o`.`plan_id` AS `plan_id`,
        `pm`.`plan_description` AS `plan`,
        `t`.`actual_date` AS `actual_date`,
        `t`.`transaction_type` AS `transaction_type`,
        (case
            when
                (`op`.`transaction_type` in ('ACTIVATION' , 'RECONNECTION',
                    'RENEWAL',
                    'Renewal',
                    'RENEWAL BEFORE AUTOEXIPIRY',
                    'RENEWAL AFTER AUTOEXIPIRY',
                    'CHANGE_PLAN'))
            then
                1
            else 0
        end) AS `Op_Active`,
        if((`t`.`transaction_type` = 'ACTIVATION'),
            1,
            0) AS `new`,
        if(((`t`.`transaction_type` = 'RECONNECTION')
                and (`op`.`transaction_type` = 'DISCONNECTION')),
            1,
            0) AS `rec`,
        if((`t`.`transaction_type` = 'CHANGE_PLAN'),
            1,
            0) AS `chg`,
        if((`t`.`transaction_type` in ('Renewal' , 'RENEWAL', 'RENEWAL AFTER AUTOEXIPIRY')),
            1,
            0) AS `ren`,
        if(((`dt`.`month_number` = month(`o`.`active_date`))
                and (`o`.`order_status` = 4)
                and (`t`.`transaction_type` in ('ACTIVATION' , 'RECONNECTION',
                'RENEWAL',
                'Renewal',
                'RENEWAL BEFORE AUTOEXIPIRY',
                'RENEWAL AFTER AUTOEXIPIRY',
                'CHANGE_PLAN'))),
            1,
            0) AS `Pending_add`,
        if(((`dt`.`month_number` >= month(`o`.`active_date`))
                and (`o`.`order_status` = 4)
                and (`op`.`transaction_type` in ('ACTIVATION' , 'RECONNECTION',
                'RENEWAL',
                'Renewal',
                'RENEWAL BEFORE AUTOEXIPIRY',
                'RENEWAL AFTER AUTOEXIPIRY',
                'CHANGE_PLAN'))),
            1,
            0) AS `Op_Pending_add`,
        if(((`t`.`transaction_type` = 'DISCONNECTION')
                and (`op`.`transaction_type` in ('ACTIVATION' , 'RECONNECTION',
                'RENEWAL',
                'Renewal',
                'RENEWAL BEFORE AUTOEXIPIRY',
                'RENEWAL AFTER AUTOEXIPIRY',
                'CHANGE_PLAN'))),
            1,
            0) AS `Del`,
        if(((`dt`.`month_number` = month(`o`.`active_date`))
                and (`o`.`order_status` = 4)
                and (`t`.`transaction_type` = 'DISCONNECTION')),
            1,
            0) AS `Pending_del`,
        if(((`dt`.`month_number` >= month(`o`.`active_date`))
                and (`o`.`order_status` = 4)),
            1,
            0) AS `Cum_Pending`,
        (case
            when
                ((`cl`.`transaction_type` in ('ACTIVATION' , 'RECONNECTION',
                    'RENEWAL',
                    'Renewal',
                    'RENEWAL BEFORE AUTOEXIPIRY',
                    'RENEWAL AFTER AUTOEXIPIRY',
                    'CHANGE_PLAN'))
                    and if((`dt`.`month_number` = month(now())),
                    (`o`.`order_status` = 1),
                    1))
            then
                1
            else 0
        end) AS `Cl_Active`
    from
        ((((((((`m_client` `c`
        join `dim_date` `dt` ON (((`dt`.`month_number` <= month(now()))
            and (`dt`.`is_first_day_in_month` = 'Yes'))))
        join `dim_date` `fdm` ON (((`fdm`.`year_month_number` = `dt`.`year_month_number`)
            and (`fdm`.`is_first_day_in_month` = 'Yes'))))
        join `dim_date` `ldm` ON (((`fdm`.`year_month_number` = `ldm`.`year_month_number`)
            and (`ldm`.`is_last_day_in_month` = 'Yes'))))
        join `b_orders` `o` ON ((`c`.`id` = `o`.`client_id`)))
        join `b_plan_master` `pm` ON ((`o`.`plan_id` = `pm`.`id`)))
        left join `b_orders_history` `op` ON (((`o`.`id` = `op`.`order_id`)
            and (`op`.`id` = (select 
                max(`h3`.`id`)
            from
                `b_orders_history` `h3`
            where
                ((`h3`.`order_id` = `o`.`id`)
                    and (`h3`.`transaction_type` in ('ACTIVATION' , 'RECONNECTION', 'RENEWAL', 'DISCONNECTION', 'Renewal', 'RENEWAL BEFORE AUTOEXIPIRY', 'RENEWAL AFTER AUTOEXIPIRY', 'CHANGE_PLAN'))
                    and (cast(`h3`.`actual_date` as date) < `fdm`.`date_value`)))))))
        left join `b_orders_history` `t` ON (((`o`.`id` = `t`.`order_id`)
            and (`t`.`id` = (select 
                max(`h4`.`id`)
            from
                `b_orders_history` `h4`
            where
                ((`h4`.`order_id` = `o`.`id`)
                    and (`h4`.`transaction_type` in ('ACTIVATION' , 'RECONNECTION', 'RENEWAL', 'DISCONNECTION', 'Renewal', 'RENEWAL BEFORE AUTOEXIPIRY', 'RENEWAL AFTER AUTOEXIPIRY', 'CHANGE_PLAN'))
                    and (cast(`h4`.`actual_date` as date) between `fdm`.`date_value` and `ldm`.`date_value`)))))))
        left join `b_orders_history` `cl` ON (((`o`.`id` = `cl`.`order_id`)
            and (`cl`.`id` = (select 
                max(`h5`.`id`)
            from
                `b_orders_history` `h5`
            where
                ((`h5`.`order_id` = `o`.`id`)
                    and (`h5`.`transaction_type` in ('ACTIVATION' , 'RECONNECTION', 'RENEWAL', 'DISCONNECTION', 'Renewal', 'RENEWAL BEFORE AUTOEXIPIRY', 'RENEWAL AFTER AUTOEXIPIRY', 'CHANGE_PLAN'))
                    and (cast(`h5`.`actual_date` as date) <= `ldm`.`date_value`)))))))
    where
        (`dt`.`year4` = year(now()));


CREATE or replace VIEW `netplnactiv_content_vw` AS
select `nav`.`year_mon` AS `Month`,
plan_id,plan,
sum(`nav`.`Op_Active`) AS `Op_Bal`,
sum(`nav`.`new`) AS `New`,
sum(`nav`.`rec`) AS `Reconn`,
sum(`nav`.`ren`) AS `Renewal`,
(sum(`nav`.`Op_Pending_add`) + sum(`nav`.`Pending_add`)) AS `Pending_add`,
sum(((((`nav`.`new` + `nav`.`rec`) + `nav`.`ren`) +
`nav`.`Op_Pending_add`) + `nav`.`Pending_add`)) AS `NetAdditions`,
sum(((((`nav`.`Op_Active` + `nav`.`new`) + `nav`.`rec`) +
`nav`.`Op_Pending_add`) + `nav`.`Pending_add`)) AS `Total`,
sum(`nav`.`Del`) AS `Deletions`,
sum(`nav`.`Pending_del`) AS `Pending_del`,
sum(`nav`.`Cum_Pending`) AS `Cum_Pending`,
(sum(`nav`.`Del`) + sum(`nav`.`Pending_del`)) AS `NetSub`,
sum(((((((`nav`.`Op_Active` + `nav`.`new`) + `nav`.`rec`) +
`nav`.`Op_Pending_add`) + `nav`.`Pending_add`) - `nav`.`Del`) -
`nav`.`Pending_del`)) AS `NetBal`,
sum(`nav`.`Op_Active` + `nav`.`new` + `nav`.`rec`   - `nav`.`Del`)  AS
`NetBalP`,
sum(`nav`.`Cl_Active`) AS `ClosingBal` ,
sum(content_cost)  content_cost ,
sum(`nav`.`Cl_Active` * content_cost) ClBal_Content,
TRUNCATE(sum(`nav`.`Cl_Active` * bop.price),2) ClBal_Subs
from `net_activedtls_vw` nav
left join  b_vendor_agmt_detail bad on ( nav.plan_id =
bad.content_code and content_cost is not null)
left join b_order_price bop on (nav.order_id = bop.order_id )
group by `nav`.`year_mon` ,plan_id,plan
order by `nav`.`month_number`;


INSERT IGNORE INTO stretchy_report VALUES(null,'Plan wise Revenue Report for Vendor', 'Table', NULL, 'Client', 'select * from netplnactiv_content_vw', 'Plan wise Revenue Report for Vendor', '0', '1');


UPDATE stretchy_report
SET report_sql='select 
    custinv.client_id as `CUST. ID`,
    custinv.name as `NAME`,
    custinv.office as `OFFICE`,
    custinv.invoiceAmount as `INVOICE AMOUNT`,
    bdr.depositAmount as `DEPOSIT AMOUNT`,
    ifnull(adjust.debitAdjustment,0) + ifnull(bdr.debitcustomerAmount,0) as `DEBIT ADJUSTMENT`,
    ifnull(adjust.creditAdjustment,0) + ifnull(bdr.creditServiceAmount,0) as `CREDIT ADJUSTMENT`,
    bdr.refundAmount as `REFUND AMOUNT`,
    ifnull(cast(truncate(sum(pay.amount_paid),2) as char),0) as `COLLECTION AMOUNT`,
    cast(truncate(ifnull(custinv.invoiceAmount,0) + ifnull(bdr.depositAmount, 0) + ifnull(adjust.debitAdjustment, 0) 
    + ifnull(bdr.debitcustomerAmount, 0) - ifnull(sum(pay.amount_paid), 0) - ifnull(creditAdjustment, 0) - ifnull(bdr.refundAmount, 0) - ifnull(bdr.creditServiceAmount, 0),2)as char) as ''BALANCE''
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
    left outer join b_payments pay ON custinv.client_id = pay.client_id AND pay.is_deleted=0
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
ORDER BY custinv.client_id' WHERE report_name='Customer Outstanding Report';

UPDATE stretchy_report
SET report_sql='select 
      t.officeName as `BRANCH`,
      t.clientId as  `CLIENT ID`,
      t.clientName as `CLIENT NAME`,
     DATE_FORMAT( t.transactionDate,''%d-%m-%Y'') as `TRANSACTION DATE`,
      t.typeOfMode as `TYPE OF MODE`,
      t.collection as` COLLECTION`
FROM
    ((select 
            off.id as officeId,
            off.name as officeName,
            clnt.id as clientId,
            clnt.display_name as clientName,
            DATE_FORMAT(pay.payment_date, ''%Y-%m-%d'') as transactionDate,
            mcv.code_value as typeOfMode,
			pay.paymode_id as modeId,
            cast(TRUNCATE(sum(ifnull(pay.amount_paid, 0)), 2) as char) as collection
    from
        m_office off
    join m_client clnt ON off.id = clnt.office_id
    join b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
    left join m_code_value mcv ON mcv.id = pay.paymode_id
    where
        off.id = clnt.office_id
    group by clientId,transactionDate,pay.paymode_id order by clientId,transactionDate) 
   union all 
  (select 
			off.id as officeId,
            off.name as officeName,
            clnt.id as clientId,
            clnt.display_name as clientName,
            DATE_FORMAT(dbr.transaction_date, ''%Y-%m-%d'') as transactionDate,
            mcv.code_value  as typeOfMode,
            dbr.refundmode_id as modeId,
            cast(TRUNCATE(-sum(ifnull(dbr.debit_amount, 0)), 2) as char) as collection
    from
        m_office off
    join m_client clnt ON off.id = clnt.office_id
    join b_deposit_refund dbr ON clnt.id = dbr.client_id
	join m_code_value mcv ON mcv.id = dbr.refundmode_id
    where
        off.id = clnt.office_id
    group by clientId ,transactionDate,dbr.refundmode_id order by clientId,transactionDate)) t 
 where (t.officeId= ''${officeId}'' or -1 = ''${officeId}'') 
  and (t.modeId= ''${paymode_id}'' or -1 = ''${paymode_id}'') 
  and t.transactionDate  between ''${startDate}'' and ''${endDate}''
 order by t.clientId,t.transactionDate' WHERE report_name='Collection Date Wise Details';

UPDATE stretchy_report SET report_sql='select
    DATE_FORMAT(pay.payment_date,''%Y-%m-%d'') AS Day,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) AS Collection
FROM
    m_office off
      JOIN
    m_client clnt ON off.id = clnt.office_id
      JOIN
    b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
      JOIN 
   m_code_value mcv ON mcv.id=pay.paymode_id
   WHERE (off.id = ''${officeId}'' or -1 = ''${officeId}'') AND (pay.paymode_id = ''${paymode_id}'' or -1 = ''${paymode_id}'' )
  AND pay.payment_date between ''${startDate}'' AND ''${endDate}''
  GROUP BY Day ORDER BY Day' WHERE report_name='Collection Day Wise chart';

UPDATE stretchy_report SET report_sql='select
    DATE_FORMAT(pay.payment_date,''%Y-%m-%d'') AS Day,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) AS Collection
FROM
    m_office off
      JOIN
    m_client clnt ON off.id = clnt.office_id
      JOIN
    b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
      JOIN 
   m_code_value mcv ON mcv.id=pay.paymode_id
   WHERE (off.id = ''${officeId}'' or -1 = ''${officeId}'') AND (pay.paymode_id = ''${paymode_id}'' or -1 = ''${paymode_id}'' )
  AND pay.payment_date between ''${startDate}'' AND ''${endDate}''
  GROUP BY Day ORDER BY Day' WHERE report_name='Collection Day Wise Summary';

UPDATE stretchy_report SET report_sql='SELECT 
     off.name AS BRANCH,
     Year(pay.payment_date) AS YEAR,
     monthname(pay.payment_date) AS Month,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) AS Collection
from
    m_office off
        join
    m_client clnt ON off.id = clnt.office_id
        join
    b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
 where (off.id = ''${officeId}'' or -1 = ''${officeId}'') GROUP BY  Month ORDER BY Year(pay.payment_date)'
WHERE report_name='Collection Month Wise Summary';

UPDATE stretchy_report SET report_sql='SELECT 
     monthname(pay.payment_date) AS Month,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) AS Collection
from
    m_office off
        join
    m_client clnt ON off.id = clnt.office_id
        join
    b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
 where (off.id = ''${officeId}'' or -1 = ''${officeId}'') GROUP BY  Month ORDER BY Year(pay.payment_date)'
WHERE report_name='Collection Month Wise chart';



UPDATE stretchy_report SET report_sql='select
    DATE_FORMAT(pay.payment_date,''%Y-%m-%d'') AS Day,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) AS Collection
FROM
    m_office off
      JOIN
    m_client clnt ON off.id = clnt.office_id
      JOIN
    b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
      JOIN 
   m_code_value mcv ON mcv.id=pay.paymode_id
   WHERE (off.id = ''${officeId}'' or -1 = ''${officeId}'') AND (pay.paymode_id = ''${paymode_id}'' or -1 = ''${paymode_id}'' )
  AND pay.payment_date between ''${startDate}'' AND ''${endDate}''
  GROUP BY Day ORDER BY Day' WHERE report_name='Collection Day Wise chart';

UPDATE stretchy_report SET report_sql='select
    DATE_FORMAT(pay.payment_date,''%Y-%m-%d'') AS Day,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) AS Collection
FROM
    m_office off
      JOIN
    m_client clnt ON off.id = clnt.office_id
      JOIN
    b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
      JOIN 
   m_code_value mcv ON mcv.id=pay.paymode_id
   WHERE (off.id = ''${officeId}'' or -1 = ''${officeId}'') AND (pay.paymode_id = ''${paymode_id}'' or -1 = ''${paymode_id}'' )
  AND pay.payment_date between ''${startDate}'' AND ''${endDate}''
  GROUP BY Day ORDER BY Day' WHERE report_name='Collection Day Wise Summary';


UPDATE stretchy_report SET report_sql='SELECT 
     off.name AS BRANCH,
     Year(pay.payment_date) AS YEAR,
     monthname(pay.payment_date) AS Month,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) AS Collection
from
    m_office off
        join
    m_client clnt ON off.id = clnt.office_id
        join
    b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
 where (off.id = ''${officeId}'' or -1 = ''${officeId}'') GROUP BY  Month ORDER BY Year(pay.payment_date)'
WHERE report_name='Collection Month Wise Summary';

UPDATE stretchy_report SET report_sql='SELECT 
     monthname(pay.payment_date) AS Month,
    TRUNCATE(sum(ifnull(pay.amount_paid, 0)),2) AS Collection
from
    m_office off
        join
    m_client clnt ON off.id = clnt.office_id
        join
    b_payments pay ON clnt.id = pay.client_id AND pay.is_deleted=0
 where (off.id = ''${officeId}'' or -1 = ''${officeId}'') GROUP BY  Month ORDER BY Year(pay.payment_date)'
WHERE report_name='Collection Month Wise chart'; 



