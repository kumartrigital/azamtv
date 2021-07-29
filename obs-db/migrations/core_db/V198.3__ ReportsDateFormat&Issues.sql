SET SQL_SAFE_UPDATES=0;

-- Cancelled view for list of cancelations report --

CREATE OR REPLACE VIEW `cancelled_vw` AS
    select distinct
        date_format(`oh`.`actual_date`, '%d-%m-%Y') AS `CANCELLATION DATE`,
        date_format(`o`.`active_date`, '%d-%m-%Y') AS `ACTIVATION DATE`,
        (to_days(`oh`.`actual_date`) - to_days(`o`.`active_date`)) AS `ACTIVE DAYS`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `ca`.`city` AS `CITY`,
        `ca`.`state` AS `STATE`,
        `ca`.`country` AS `COUNTRY`,
        `pm`.`plan_description` AS `PLAN`,
        `o`.`billing_frequency` AS `BILL FREQUENCY`,
        format(sum(`op`.`price`), 2) AS `PRICE`
    from
        ((((((`m_client` `c`
        join `b_client_address` `ca` ON ((`c`.`id` = `ca`.`client_id`)))
        join `m_office` `of` ON ((`c`.`office_id` = `of`.`id`)))
        join `b_orders` `o` ON ((`c`.`id` = `o`.`client_id`)))
        join `b_orders_history` `oh` ON ((`o`.`id` = `oh`.`order_id`)))
        left join `b_plan_master` `pm` ON ((`o`.`plan_id` = `pm`.`id`)))
        left join `b_order_price` `op` ON ((`o`.`id` = `op`.`order_id`)))
    where
        (`oh`.`transaction_type` = 'Cancelled');

-- City wise orders Report view --

CREATE OR REPLACE VIEW `city_order_plan_vw` AS
    select 
        `ca`.`country` AS `COUNTRY`,
        `ca`.`city` AS `CITY`,
        `of`.`name` AS `BRANCH`,
        `c`.`display_name` AS `CLIENT NAME`,
        `pm`.`plan_description` AS `PLAN NAME`,
        `o`.`order_no` AS `ORDER NO`,
        `ev`.`enum_value` AS `ORDER STATUS`,
        `bcp`.`contract_period` AS `CONTRACT PERIOD`,
        date_format(`o`.`start_date`, '%d-%m-%Y') AS `START DATE`,
        date_format(`o`.`end_date`, '%d-%m-%Y') AS `END DATE`,
        `o`.`billing_frequency` AS `BILLING FREQUENCY`,
        format(sum(`bop`.`price`), 2) AS `PRICE`
    from
        (((((((`b_orders` `o`
        left join `r_enum_value` `ev` ON (((`o`.`order_status` = `ev`.`enum_id`)
            and (`ev`.`enum_name` = 'order_status'))))
        left join `b_order_price` `bop` ON ((`o`.`id` = `bop`.`order_id`)))
        left join `b_contract_period` `bcp` ON ((`o`.`contract_period` = `bcp`.`id`)))
        left join `m_client` `c` ON ((`o`.`client_id` = `c`.`id`)))
        left join `b_plan_master` `pm` ON ((`o`.`plan_id` = `pm`.`id`)))
        left join `m_office` `of` ON ((`c`.`office_id` = `of`.`id`)))
        left join `b_client_address` `ca` ON (((`ca`.`client_id` = `c`.`id`)
            and (`ca`.`address_key` = 'PRIMARY'))))
    where
        (`o`.`is_deleted` = 'n')
    group by `o`.`id`;


-- List of Discconect order's report view --

CREATE OR REPLACE VIEW `discon_vw` AS
    select distinct
        `mo`.`name` AS `BRANCH`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `c`.`phone` AS `PHONE NO`,
        `pm`.`plan_description` AS `PLAN`,
        `bcp`.`contract_period` AS `CONTRACT PERIOD`,
        `o`.`billing_frequency` AS `BILL FRQUENCY`,
        date_format(`c`.`activation_date`, '%d-%m-%Y') AS `ACTIVATION DATE`,
        date_format(`oh`.`actual_date`, '%d-%m-%Y') AS `DISCONNECTION DATE`,
        (to_days(`oh`.`actual_date`) - to_days(`c`.`activation_date`)) AS `NO OF DAYS`,
        format(sum(`op`.`price`), 2) AS `PRICE`,
        `o`.`disconnect_reason` AS `DISCONNECT REASON`,
        (select 
                max(`x`.`hw_serial_no`)
            from
                `b_association` `x`
            where
                ((`c`.`id` = `x`.`client_id`)
                    and (`x`.`is_deleted` = 'N'))) AS `DEVICE`
    from
        (((((((`m_client` `c`
        join `m_office` `mo` ON ((`c`.`office_id` = `mo`.`id`)))
        join `b_client_address` `ca` ON ((`c`.`id` = `ca`.`client_id`)))
        join `b_orders` `o` ON ((`c`.`id` = `o`.`client_id`)))
        left join `b_contract_period` `bcp` ON ((`o`.`contract_period` = `bcp`.`id`)))
        join `b_orders_history` `oh` ON ((`o`.`id` = `oh`.`order_id`)))
        left join `b_plan_master` `pm` ON ((`o`.`plan_id` = `pm`.`id`)))
        left join `b_order_price` `op` ON ((`o`.`id` = `op`.`order_id`)))
    where
        ((`o`.`id` = (select 
                max(`o2`.`id`)
            from
                `b_orders` `o2`
            where
                ((`o2`.`client_id` = `o`.`client_id`)
                    and (`o2`.`user_action` = 'DISCONNECTION'))))
            and (`oh`.`id` = (select 
                max(`oh2`.`id`)
            from
                `b_orders_history` `oh2`
            where
                ((`oh2`.`transaction_type` = 'DISCONNECTION')
                    and (`oh2`.`order_id` = `oh`.`order_id`)))))
    group by `o`.`id`;


-- List of hardware allocations report view --

CREATE OR REPLACE VIEW `hw_alloc_vw` AS
    select distinct
        `mo`.`name` AS `BRANCH`,
        `c`.`id` AS `CLIENT ID`,
        `c`.`display_name` AS `CLIENT NAME`,
        `im`.`item_code` AS `ITEM CODE`,
        `im`.`item_description` AS `DESCRIPTION`,
        `a`.`serial_no` AS `SERIAL NO`,
        `id`.`provisioning_serialno` AS `PROVISIONING NO`,
        date_format(`a`.`allocation_date`, '%d-%m-%Y') AS `ALLOCATION DATE`,
        date_format(`bos`.`sale_date`, '%d-%m-%Y') AS `SALE DATE`,
        truncate(`bos`.`total_price`, 2) AS `SALE PRICE`,
        `id`.`status` AS `STATUS`,
        `im`.`warranty` AS `WARRANTY`,
        date_format(`id`.`warranty_date`, '%d-%m-%Y') AS `WARRANTY EXPIRYDATE`
    from
        ((((((`m_client` `c`
        join `b_client_address` `ca` ON (((`c`.`id` = `ca`.`client_id`)
            and (`ca`.`address_key` = 'PRIMARY'))))
        join `b_allocation` `a` ON (((`c`.`id` = `a`.`client_id`)
            and (`a`.`is_deleted` = 'N'))))
        join `b_item_detail` `id` ON ((`id`.`serial_no` = `a`.`serial_no`)))
        join `b_item_master` `im` ON ((`id`.`item_master_id` = `im`.`id`)))
        join `b_onetime_sale` `bos` ON (((`bos`.`client_id` = `id`.`client_id`)
            and (`bos`.`item_id` = `im`.`id`))))
        join `m_office` `mo` ON ((`c`.`office_id` = `mo`.`id`)));

-- List of New Customers Report view --
CREATE OR REPLACE VIEW `new_clients_vw` AS
    select distinct
        `of`.`name` AS `BRANCH`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `ca`.`city` AS `CITY`,
        `ca`.`state` AS `STATE`,
        `ca`.`country` AS `COUNTRY`,
        `pm`.`plan_description` AS `PLAN`,
        date_format(`c`.`activation_date`, '%d-%m-%Y') AS `ACTIVATION DATE`,
        `a`.`serial_no` AS `SERIAL NO`,
        `o`.`billing_frequency` AS `BILL FRQUENCY`,
        `op`.`price` AS `ORDER PRICE`,
        `mcv`.`code_value` AS `ORDER STATUS`
    from
        (((((((`m_client` `c`
        join `m_office` `of` ON ((`c`.`office_id` = `of`.`id`)))
        join `b_client_address` `ca` ON ((`c`.`id` = `ca`.`client_id`)))
        left join `b_orders` `o` ON ((`c`.`id` = `o`.`client_id`)))
        left join `b_plan_master` `pm` ON ((`o`.`plan_id` = `pm`.`id`)))
        left join `b_order_price` `op` ON ((`o`.`id` = `op`.`order_id`)))
        left join `m_code_value` `mcv` ON (((`o`.`order_status` = `mcv`.`order_position`)
            and (`mcv`.`code_id` = 40))))
        left join `b_allocation` `a` ON ((`a`.`order_id` = `o`.`id`)));


-- List of renewal orders report view --

CREATE OR REPLACE VIEW `renewal_vw` AS
    select 
        `mo`.`name` AS `BRANCH`,
        `pm`.`plan_description` AS `PLAN`,
        date_format(`oh`.`transaction_date`, '%d-%m-%Y') AS `RENEWAL DATE`,
        date_format(`o`.`active_date`, '%d-%m-%Y') AS `ACTIVATION DATE`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `ca`.`city` AS `CITY`,
        `ca`.`state` AS `STATE`,
        `ca`.`country` AS `COUNTRY`,
        `o`.`billing_frequency` AS `BILL FRQUENCY`,
        format(sum(`op`.`price`), 2) AS `PRICE`,
        `u`.`username` AS `USER NAME`
    from
        (((((((`m_client` `c`
        join `m_office` `mo` ON ((`c`.`office_id` = `mo`.`id`)))
        join `b_client_address` `ca` ON ((`c`.`id` = `ca`.`client_id`)))
        join `b_orders` `o` ON ((`c`.`id` = `o`.`client_id`)))
        join `b_orders_history` `oh` ON ((`o`.`id` = `oh`.`order_id`)))
        join `m_appuser` `u` ON ((`oh`.`createdby_id` = `u`.`id`)))
        left join `b_plan_master` `pm` ON ((`o`.`plan_id` = `pm`.`id`)))
        left join `b_order_price` `op` ON ((`o`.`id` = `op`.`order_id`)))
    where
        ((`oh`.`transaction_type` = 'Renewal')
            or (`oh`.`transaction_type` = 'RENEWAL AFTER AUTOEXIPIRY')
            or (`oh`.`transaction_type` = 'RENEWAL BEFORE AUTOEXIPIRY'))
    group by `o`.`id`
    order by `oh`.`id` desc;

-- Stock details report view --
CREATE OR REPLACE VIEW `stock_available_vw` AS
    select 
        `o`.`name` AS `BRANCH`,
        `s`.`supplier_description` AS `SUPPLIER`,
        `im`.`item_code` AS `ITEM CODE`,
        `im`.`item_description` AS `ITEM NAME`,
        `id`.`serial_no` AS `SERIAL NO`,
        `id`.`provisioning_serialno` AS `PROV NO`,
        `id`.`quality` AS `QUALITY`,
        date_format(`g`.`purchase_date`, '%d-%m-%Y') AS `PURCHASE DATE`
    from
        (((((`b_grn` `g`
        join `b_supplier` `s` ON ((`g`.`supplier_id` = `s`.`id`)))
        join `b_item_master` `im` ON ((`g`.`item_master_id` = `im`.`id`)))
        join `b_item_detail` `id` ON (((`im`.`id` = `id`.`item_master_id`)
            and (`id`.`status` = 'Available'))))
        left join `m_office` `o` ON ((`id`.`office_id` = `o`.`id`)))
        left join `m_code_value` `mcv` ON (((`mcv`.`code_id` = 46)
            and (`mcv`.`id` = `o`.`office_type`))))
    group by `id`.`serial_no`;


-- Stock Summary --

CREATE OR REPLACE
VIEW `br_stock_summary` AS
    select 
        `s`.`supplier_description` AS `Supplier`,
        `g`.`office_id` AS `OfficeId`,
        (select 
                `o`.`name`
            from
                `m_office` `o`
            where
                (`o`.`id` = `g`.`office_id`)) AS `Receiver`,
        `im`.`item_description` AS `Item`,
        date_format(`g`.`purchase_date`, '%Y-%M') AS `PurchaseMonth`,
        cast(`g`.`orderd_quantity` as char charset latin1) AS `OrderedQty`,
        cast(`g`.`received_quantity` as char charset latin1) AS `ReceivedQty`,
        cast(`mrn`.`received_quantity` as char charset latin1) AS `TransferQty`,
        cast(sum(ifnull(`ots`.`quantity`, 0)) as char charset latin1) AS `SaleQty`,
        cast(((`g`.`received_quantity` - sum(ifnull(`ots`.`quantity`, 0))) - ifnull(`mrn`.`received_quantity`, 0))
            as char charset latin1) AS `StockBal`
    from
        ((((`b_grn` `g`
        left join `b_supplier` `s` ON ((`g`.`supplier_id` = `s`.`id`)))
        left join `b_item_master` `im` ON ((`g`.`item_master_id` = `im`.`id`)))
        left join `b_mrn` `mrn` ON (((`im`.`id` = `mrn`.`item_master_id`)
            and (`mrn`.`from_office` = `g`.`office_id`))))
        left join `b_onetime_sale` `ots` ON (((`im`.`id` = `ots`.`item_id`)
            and (`ots`.`office_id` = `g`.`office_id`)
            and (`ots`.`is_deleted` = 'N')))) 
    union all select 
        (select 
                `o`.`name`
            from
                `m_office` `o`
            where
                (`o`.`id` = `mrn`.`from_office`)) AS `Supplier`,
        coalesce(`mrn`.`to_office`, `ots`.`office_id`) AS `OfficeId`,
        (select 
                `o`.`name`
            from
                `m_office` `o`
            where
                (`o`.`id` = `mrn`.`to_office`)) AS `Receiver`,
        `im`.`item_description` AS `Item`,
        date_format(`mrn`.`requested_date`, '%Y-%M') AS `PurchaseMonth`,
        cast(`mrn`.`orderd_quantity` as char charset latin1) AS `OrderedQty`,
        cast(`mrn`.`received_quantity` as char charset latin1) AS `ReceivedQty`,
        cast(ifnull(`mrf`.`received_quantity`, 0) as char charset latin1) AS `TransferQty`,
        cast(sum(ifnull(`ots`.`quantity`, 0)) as char charset latin1) AS `SaleQty`,
        cast(((`mrn`.`received_quantity` - sum(ifnull(`ots`.`quantity`, 0))) - ifnull(`mrf`.`received_quantity`, 0))
            as char charset latin1) AS `StockBal`
    from
        (((`b_item_master` `im`
        join `b_mrn` `mrn` ON ((`im`.`id` = `mrn`.`item_master_id`)))
        left join `b_mrn` `mrf` ON (((`im`.`id` = `mrf`.`item_master_id`)
            and (`mrn`.`to_office` = `mrf`.`from_office`))))
        left join `b_onetime_sale` `ots` ON (((`im`.`id` = `ots`.`item_id`)
            and (`ots`.`office_id` = `mrn`.`to_office`))))
    group by `ots`.`office_id`;



UPDATE stretchy_report SET core_report=0 WHERE report_type='table';


UPDATE stretchy_report SET report_sql='select a.* from city_order_plan_vw a,m_office o where a.branch=o.name and  
DATE_FORMAT(STR_TO_DATE(`START DATE`,''%d-%m-%Y''),''%Y-%m-%d'') between ''${startDate}'' and ''${endDate}'' and  
(o.id = ''${officeId}'' or -1 = ''${officeId}'')' WHERE report_name='City Wise Orders';


UPDATE stretchy_report SET report_sql='select * FROM cancelled_vw 
where date_format(str_to_date(`CANCELLATION DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  between ''${startDate}'' and ''${endDate}'''
WHERE report_name='List of Cancellations';

UPDATE stretchy_report SET report_sql='SELECT 
    o.name AS BRANCH,
    a.account_no AS `ACCOUNT NO`,
    CASE
	    WHEN status_enum = 100 THEN ''New''
        WHEN status_enum = 300 THEN ''Active''
        WHEN status_enum = 600 THEN ''Deactive''
        ELSE status_enum
    END as `CLIENT STATUS`,
    a.display_name AS `CLIENT NAME`,
    im.item_description AS DEVICE,
    DATE_FORMAT(b.sale_date, ''%d-%m-%Y'') `SALE DATE`,
    FORMAT(b.total_price,2) AS `SALE PRICE`
FROM
     m_office o join m_client a on  o.id=a.office_id
     join b_onetime_sale b on  b.client_id = a.id
     join  b_item_master im on b.item_id = im.id
WHERE
 o.id= ''${officeId}''  or -1 = ''${officeId}''  and b.sale_date between ''${startDate}'' and ''${endDate}''
order by b.sale_date' WHERE report_name='List of Device Sales';


UPDATE stretchy_report SET report_sql='select * from discon_vw  dv where 
((select o.id from m_office o where o.name=dv.BRANCH) = ''${officeId}'' or -1 = ''${officeId}'') and
date_format(str_to_date(`DISCONNECTION DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  between ''${startDate}'' and ''${endDate}'''
WHERE report_name='List of Disconnections';


UPDATE stretchy_report SET report_sql='select * from new_clients_vw a where  
((select o.id from m_office o where o.name=a.BRANCH ) = ''${officeId}'' or -1 = ''${officeId}'') and
date_format(str_to_date(`ACTIVATION DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  between ''${startDate}'' and ''${endDate}'''
WHERE report_name='List of New Customers';

UPDATE stretchy_report SET report_sql='select * FROM renewal_vw where date_format(str_to_date( `RENEWAL DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  
between ''${startDate}'' and ''${endDate}''' WHERE report_name='List of Renewals';

UPDATE stretchy_report SET report_sql='select 
    plan.plan_description As `PLAN DESCRIPTION`,
    plan.service_code as `SERVICE CODE`,
    plan.enum_value as `STATUS`,
    plan.charge_code as `CHARGE CODE`,
    plan.charge_type as `CHARGE TYPE`,
    plan.active_since as `ACTIVE SINCE`,
    format(plan.price,2) as `PRICE`
from
    (SELECT 
           plnmstr.id as plan_id,
            plnmstr.plan_description,
            plnprc.service_code,
            ev.enum_value,
            plnprc.charge_code,
            chrgcds.charge_type,
            concat(chrgcds.charge_duration, chrgcds.duration_type) as Active_Since,
            plnprc.price
    FROM
        b_plan_master plnmstr
    join b_plan_pricing plnprc ON plnmstr.id = plnprc.plan_id
    join b_charge_codes chrgcds ON plnprc.charge_code = chrgcds.charge_code
	join r_enum_value ev ON ev.enum_id = plnmstr.plan_status and ev.enum_name=''order_status''
    where
        plnprc.is_deleted = ''n''
            and plnmstr.is_deleted = ''n'' union all SELECT 
            plndtl.plan_id,
            plnmstr.plan_description,
            plndtl.service_code, 
            ev.enum_value,
            plnprc.charge_code,
            chrgcds.charge_type,
            concat(chrgcds.charge_duration, '' '', chrgcds.duration_type) as Active_Since,
            plnprc.price
    FROM
        b_plan_master plnmstr
    join b_plan_detail plndtl ON plnmstr.id = plndtl.plan_id
    join b_plan_pricing plnprc ON plndtl.plan_id = plnprc.plan_id and plndtl.service_code = plnprc.service_code
    join b_charge_codes chrgcds ON plnprc.charge_code = chrgcds.charge_code
    join r_enum_value ev ON ev.enum_id = plnmstr.plan_status and ev.enum_name=''order_status''
    where
        plndtl.is_deleted = ''n''
            and plnmstr.is_deleted = ''n'') as plan
order by plan.plan_id , plan.service_code , plan.charge_code' WHERE report_name='Plan wise services';



UPDATE stretchy_report SET report_sql='SELECT BRANCH,
cast(round(sum(if(ledger_type=''HW-SALE'',amount,0)) ,2) AS char ) AS "HW SALES",
cast(round(sum(if(ledger_type=''ORDER'',amount,0)) ,2) AS char ) AS "SUBSCRIPTION SALES",
cast(round(sum(amount) ,2) AS char ) as TOTAL
FROM customer_ledger_vw2 a WHERE a.ledger_type IN (''HW-SALE'',''ORDER'')
and  a.date between ''${startDate}'' and ''${endDate}''
GROUP BY BRANCH' WHERE report_name='Sales Activity Report';



UPDATE stretchy_report SET report_sql='select * from hw_alloc_vw  hw where
( (select o.id from m_office o where o.name=hw.BRANCH) = ''${officeId}'' or -1 = ''${officeId}'') and  date_format(str_to_date(`ALLOCATION DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  
between ''${startDate}'' and ''${endDate}''' WHERE report_name='List of HardWare Allocations';


UPDATE stretchy_report SET report_sql='select * from stock_available_vw sv where ((select o.id from m_office o where o.name=sv.BRANCH) = ''${officeId}'' or -1 = ''${officeId}'')  
and DATE_FORMAT(STR_TO_DATE(`PURCHASE DATE`,''%d-%m-%Y''),''%Y-%m-%d'') between ''${startDate}'' and ''${endDate}'''
WHERE report_name='Stock Item Details';


-- CLIENT --

UPDATE stretchy_report SET report_sql='select 
    custinv.client_id as `CUST. ID`,
    custinv.name as `NAME`,
    custinv.office as `OFFICE`,
    custinv.invoiceAmount as `INVOICE AMOUNT`,
    bdr.depositAmount as `DEPOSIT AMOUNT`,
    (adjust.debitAdjustment) + (bdr.debitcustomerAmount) as `DEBIT ADJUSTMENT`,
    (adjust.creditAdjustment) + (bdr.creditServiceAmount) as `CREDIT ADJUSTMENT`,
    bdr.refundAmount as `REFUND AMOUNT`,
    ifnull(cast(round(sum(pay.amount_paid), 2) as char),0) as `COLLECTION AMOUNT`,
    cast(round(ifnull(custinv.invoiceAmount,0) + ifnull(bdr.depositAmount, 0) + ifnull(adjust.debitAdjustment, 0) 
    + ifnull(bdr.debitcustomerAmount, 0) - ifnull(sum(pay.amount_paid), 0) - ifnull(creditAdjustment, 0) - ifnull(bdr.refundAmount, 0) - ifnull(bdr.creditServiceAmount, 0))as char) as ''BALANCE''
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
ORDER BY custinv.client_id' WHERE report_name='Customer Outstanding Report'; 


-- List of Bills --

CREATE OR REPLACE
VIEW `bill_list_vw` AS
    select 
        `bm`.`id` AS `BILL ID`,
        DATE_FORMAT(`bm`.`Bill_date`,'%d-%m-%Y') AS `BILL DATE`,
       -- `bm`.`Bill_No` AS `BILL NO`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `ca`.`city` AS `CITY`,
        `ca`.`state` AS `STATE`,
        `ca`.`country` AS `COUNTRY`,
        -- `bm`.`Bill_Period` AS `BILL PERIOD`,
        cast(TRUNCATE(max(if((`bd`.`Transaction_type` = 'SERVICE_CHARGES'),
                `bd`.`Amount`,
                0)),2)
            as char charset utf8) AS `SERVICE CHARGES`,
      cast(TRUNCATE(max(if((`bd`.`Transaction_type` = 'TAXES'),
                `bd`.`Amount`,
                0)),2)
            as char charset utf8) AS `TAXES`,
      cast(TRUNCATE(max(if((`bd`.`Transaction_type` = 'ADJUSTMENT'),
                `bd`.`Amount`,
                0)),2)
            as char charset utf8) AS `ADJUSTMENT`,
         cast(TRUNCATE(max(if((`bd`.`Transaction_type` LIKE CONCAT('%','PAYMENT','%')),
                `bd`.`Amount`,
                0)),2)
            as char charset utf8) AS `PAYMENT`,
       cast(TRUNCATE(max(if((`bd`.`Transaction_type` = 'ONETIME_CHARGES'),
                `bd`.`Amount`,
                0)),2)
            as char charset utf8) AS `ONETIME CHARGES`,
        cast(TRUNCATE(`bm`.`Due_amount`,2) as char charset utf8) AS `BILL AMOUNT`
    from
        (((`m_client` `c`
        join `b_client_address` `ca` ON ((`c`.`id` = `ca`.`client_id`)))
        join `b_bill_master` `bm` ON ((`c`.`id` = `bm`.`Client_id`) AND `bm`.`is_deleted`='N'))
        join `b_bill_details` `bd` ON ((`bm`.`id` = `bd`.`Bill_id`)))
    group by `bd`.`Bill_id`;


-- list of unpaid bills --

CREATE OR REPLACE VIEW `unpaid_bills_vw` AS
    select 
        `o`.`name` AS `BRANCH`,
        `ca`.`city` AS `CITY`,
        `ca`.`street` AS `STREET`,
        `ca`.`state` AS `STATE`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `c`.`category_type` AS `TYPE`,
        DATE_FORMAT(`c`.`activation_date`, '%d-%m-%Y') AS `ACTIVATION DATE`,
        DATE_FORMAT(`bm`.`Bill_date`,'%d-%m-%Y') AS `BILL DATE`,
        cast(round(`bm`.`Due_amount`, 2) as char charset utf8) AS `DUE AMT`,
        DATE_FORMAT(`bm`.`Due_date`, '%d-%m-%Y') AS `DUE DATE`
    from
        ((((`b_bill_master` `bm`
        join `m_client` `c` ON ((`c`.`id` = `bm`.`Client_id`)))
        left join `b_client_address` `ca` ON ((`ca`.`client_id` = `c`.`id`)))
        left join `m_office` `o` ON ((`o`.`id` = `c`.`office_id`)))
        left join `b_payments` `p` ON ((`p`.`bill_id` = `bm`.`id`)))
    where
        isnull(`p`.`bill_id`);


UPDATE stretchy_report SET report_sql='select * FROM bill_list_vw where 
DATE_FORMAT(STR_TO_DATE(`BILL DATE` ,''%d-%m-%Y''),''%Y-%m-%d'') between ''${startDate}'' and ''${endDate}'''
WHERE report_name='List of Bills';


UPDATE stretchy_report SET report_sql='select * from unpaid_bills_vw where 
DATE_FORMAT(STR_TO_DATE(`DUE DATE`,''%d-%m-%Y''),''%Y-%m-%d'') between ''${startDate}'' and ''${endDate}'''
WHERE report_name='Unpaid Bills';

UPDATE stretchy_report SET report_sql='SELECT   `RESELLER ID`,`RESELLER NAME`,`COUNTRY`,  `PLAN NAME` AS `PLAN/ITEAM`, `MONTH`, 
`SOURCE`,`SHARE TYPE`,count(`CLIENT ID`) `NO OF UNITS`,  
cast(round(sum(`CHARGE AMOUNT`),2) as char) `CHARGE AMOUNT`,`SHARE AMOUNT`,
cast(round(sum(`COMM AMOUNT`),2) as char) `COMMISSION`
FROM v_agent_commission
WHERE `INVOICE DATE`  BETWEEN ''${startDate}'' and ''${endDate}''
GROUP BY `PLAN/ITEAM`,SOURCE,MONTH,`RESELLER ID`,`SHARE TYPE`' WHERE report_name='Reseller Commission Summary';


-- Ticket --


--  Client Wise Tickets  --
CREATE OR REPLACE VIEW `client_tkt_dtls_vw` AS
select 
        `o`.`name` AS `BRANCH`,
        `c`.`id` AS `CLIENT ID`,
        `c`.`display_name` AS `CLIENT NAME`,
        date_format(`tm`.`ticket_date`, '%d-%m-%Y') AS `TICKET DATE`,
        `tm`.`description` AS `DESCRIPTION`,
        `tm`.`status` AS `STATUS`,
        date_format(`tm`.`closed_date`, '%d-%m-%Y') AS `CLOSED DATE`,
        time_format(timediff(`tm`.`closed_date`, `tm`.`ticket_date`),
                '%H:%i:%s') AS `RESOL TIME`,
        `td`.`comments` AS `COMMENTS`,
        `u2`.`username` AS `ASSIGNED TO`,
        `u`.`username` AS `CREATED USER`
    from
        (((((`b_ticket_details` `td`
        left join `m_appuser` `u` ON ((`td`.`createdby_id` = `u`.`id`)))
        left join `m_appuser` `u2` ON ((`td`.`assigned_to` = `u2`.`id`)))
        left join `b_ticket_master` `tm` ON ((`td`.`ticket_id` = `tm`.`id`)))
        left join `m_client` `c` ON ((`tm`.`client_id` = `c`.`id`)))
        left join `m_office` `o` ON ((`o`.`id` = `c`.`office_id`))) order by `tm`.`ticket_date` desc;

UPDATE stretchy_report SET report_sql='select * from client_tkt_dtls_vw tkt where
((select o.id from m_office o where o.name=tkt.BRANCH) = ''${officeId}'' or -1 = ''${officeId}'') 
and  date_format(str_to_date(`TICKET DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  
between ''${startDate}'' and ''${endDate}''' WHERE report_name='Client Wise Tickets';

-- Ticket Details --
CREATE OR REPLACE
VIEW `tkt_dtls_vw` AS
    select 
        `c`.`office_id` AS `OFFICE ID`,
        `o`.`name` AS `BRANCH`,
        `c`.`id` AS `CLIENT ID`,
        `c`.`display_name` AS `CLIENT`,
        date_format(`tm`.`ticket_date`, '%d-%m-%Y') AS `TICKET DATE`,
        `tm`.`description` AS `DESCRIPTION`,
        `tm`.`status` AS `STATUS`,
        date_format(`tm`.`closed_date`, '%d-%m-%Y') AS `CLOSED DATE`,
        time_format(timediff(`tm`.`closed_date`, `tm`.`ticket_date`),
                '%H:%i:%s') AS `RESOL TIME`,
        `td`.`comments` AS `COMMENTS`,
        `u2`.`username` AS `ASSIGNED TO`,
        `u`.`username` AS `CREATED USER`
    from
        (((((`b_ticket_details` `td`
        left join `m_appuser` `u` ON ((`td`.`createdby_id` = `u`.`id`)))
        left join `m_appuser` `u2` ON ((`td`.`assigned_to` = `u2`.`id`)))
        left join `b_ticket_master` `tm` ON ((`td`.`ticket_id` = `tm`.`id`)))
        left join `m_client` `c` ON ((`tm`.`client_id` = `c`.`id`)))
        left join `m_office` `o` ON ((`o`.`id` = `c`.`office_id`))) order by `tm`.`ticket_date` desc;

UPDATE stretchy_report SET report_sql='select * from tkt_dtls_vw where
 date_format(str_to_date(`TICKET DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  
between ''${startDate}'' and ''${endDate}'' 
and  (`OFFICE ID` = ''${officeId}'' or -1 = ''${officeId}'')' WHERE report_name='Ticket Details';



--  INVOICE CATEGORY -- 

-- Plan Wise Revenue Date Wise Details --

UPDATE stretchy_report SET report_sql='SELECT plnmst.plan_description AS `PLAN`,
invc.id AS `INV.NO`,
DATE_FORMAT(invc.Invoice_date,''%d-%m-%Y'') AS `INVOICE DATE`,
chrg.client_id AS `CLIENTID`,
clnt.display_name `CLIENT NAME`,
DATE_FORMAT(chrg.charge_start_date,''%d-%m-%Y'') as `FROM`,
DATE_FORMAT(chrg.charge_end_date,''%d-%m-%Y'') as `TO`,
cast(round(chrg.charge_amount,2) as char) `CHARGE AMT`,
cast(round(chrg.discount_amount,2)as char) `DISCOUNT AMT`,
cast(round(ifnull(tax.Tax_amount,0),2) as char)  `TAX AMT`,
cast(round(invc.invoice_amount,2) as char) `INVOICE AMT` 
FROM b_plan_master plnmst 
JOIN b_orders ord ON plnmst.id = ord.plan_id 
JOIN b_charge chrg ON chrg.order_id = ord.id 
JOIN b_invoice invc ON invc.client_id = chrg.client_id AND invc.id = chrg.invoice_id AND charge_type = ''RC''
JOIN m_client clnt ON clnt.id = invc.client_id 
LEFT OUTER JOIN b_charge_tax tax ON tax.charge_id = chrg.id 
WHERE  (plnmst.id = ''${planId}'' or -1 = ''${planId}'') and invc.Invoice_date between ''${startDate}'' AND ''${endDate}''
GROUP BY plnmst.id,invc.id ORDER BY invc.id' WHERE report_name='Plan Wise Revenue Date Wise Details';


UPDATE stretchy_report SET report_sql='SELECT 
    plnmst.plan_description AS `PLAN`,
    DATE_FORMAT(invc.Invoice_date, ''%d-%m-%Y'') AS `INVOICE DATE`,
    cast(sum(truncate(chrg.charge_amount, 2)) as char) `CHARGE AMOUNT`,
    cast(sum(truncate(chrg.discount_amount, 2)) as char) `DISCOUNT AMOUNT`,
    cast(sum(truncate(ifnull(tax.Tax_amount, 0), 2)) as char) `TAX AMOUNT`,
    cast(sum(truncate(invc.invoice_amount, 2)) as char) `INVOICE AMOUNT`
FROM
    b_plan_master plnmst
        JOIN
    b_orders ord ON plnmst.id = ord.plan_id
        JOIN
    b_charge chrg ON chrg.order_id = ord.id
        JOIN
    b_invoice invc ON invc.client_id = chrg.client_id AND invc.id = chrg.invoice_id AND charge_type = ''RC''
        JOIN
    m_client clnt ON clnt.id = invc.client_id
        LEFT OUTER JOIN
    b_charge_tax tax ON tax.charge_id = chrg.id
WHERE
  (plnmst.id = ''${planId}'' or - 1 = ''${planId}'') and invc.Invoice_date between ''${startDate}'' and ''${endDate}''
GROUP BY `INVOICE DATE`,`PLAN` order by `INVOICE DATE`' WHERE report_name='Plan Wise Revenue DayWise Summary';

UPDATE stretchy_report SET report_sql='SELECT 
    plnmst.plan_description AS PLAN,
    Monthname(invc.Invoice_date) AS MONTH,
    YEAR(invc.Invoice_date) AS YEAR,
    cast(round(sum(chrg.charge_amount), 2) as char) `CHARGE AMOUNT`,
    cast(round(sum(chrg.discount_amount), 2) as char) `DISCOUNT AMOUNT`,
    cast(round(sum(ifnull(tax.Tax_amount, 0)), 2) as char) `TAX AMOUNT`,
    cast(round((sum(chrg.charge_amount) - sum(chrg.discount_amount) + sum(ifnull(tax.Tax_amount, 0))),2)
        as char) AS `INVOICE AMOUNT`
FROM
    b_plan_master plnmst
        JOIN
    b_orders ord ON plnmst.id = ord.plan_id
        JOIN
    b_charge chrg ON chrg.order_id = ord.id
        JOIN
    b_invoice invc ON invc.client_id = chrg.client_id AND invc.id = chrg.invoice_id AND charge_type = ''RC''
        JOIN
    m_client clnt ON clnt.id = invc.client_id
        LEFT OUTER JOIN
    b_charge_tax tax ON tax.charge_id = chrg.id
 WHERE
    (plnmst.id = ''${planId}'' or - 1 = ''${planId}'')
GROUP BY Monthname(invc.Invoice_date), plnmst.id' WHERE report_name='Plan Wise Revenue MonthWise Details';


UPDATE stretchy_report SET report_sql=
'SELECT 
      t.branch AS `BRANCH`,
      t.clientId AS  `CLIENT ID`,
      t.clientName AS `CLIENT NAME`,
      DATE_FORMAT( t.adjustmentDate,''%d-%m-%Y'') as `ADJUSTMENT DATE`,
      t.typeOfMode AS `ADJUSTMENT CODE`,
      t.typeofAdjust AS`TYPE OF ADJUST`,
	  truncate(t.debit,2) AS `DEBIT AMOUNT`,
	  truncate(t.credit,2) AS `CREDIT AMOUNT`
FROM
    ((SELECT 
    off.id as officeId,
    off.name AS branch,
    clnt.id AS  clientId,
    clnt.display_name AS clientName,
    DATE_FORMAT(adj.adjustment_date, ''%Y-%m-%d'') AS adjustmentDate,
    mcv.code_value as  typeOfMode,
    adjustment_type AS typeofAdjust,
    SUM((CASE adjustment_type
        WHEN ''DEBIT'' THEN adjustment_amount
        ELSE NULL
    END)) AS  debit,
    SUM((CASE adjustment_type
        WHEN ''CREDIT'' THEN adjustment_amount
        ELSE NULL
    END)) AS credit
from
    m_office off
        JOIN
    m_client clnt ON off.id = clnt.office_id
        JOIN
    b_adjustments adj ON clnt.id = adj.client_id
       LEFT JOIN
    m_code_value mcv ON mcv.id = adj.adjustment_code
WHERE off.id=clnt.office_id
GROUP BY clientId,typeofAdjust,adjustmentDate ORDER BY clientId,adjustmentDate)
UNION ALL
(SELECT 
    off.id as officeId,
    off.name AS branch,
    clnt.id AS  clientId,
    clnt.display_name AS clientName,
    DATE_FORMAT(dbr.transaction_date, ''%Y-%m-%d'') AS adjustmentDate,
    ''Service Balance Adjustment'' AS typeOfMode ,
     (CASE  dbr.description WHEN ''Payment Towards Refund Entry'' THEN ''DEBIT'' 
     ELSE ''CREDIT'' END) AS typeofAdjust,
     SUM((CASE dbr.description
        WHEN ''Payment Towards Refund Entry'' THEN debit_amount
        ELSE NULL
    END)) AS debit,
     SUM((CASE dbr.description
        WHEN ''Refund Adjustment towards Service Balance'' THEN credit_amount
        ELSE NULL
    END)) AS credit
    FROM
	 m_office off
        JOIN
    m_client clnt ON off.id = clnt.office_id
        JOIN
     b_deposit_refund dbr ON clnt.id = dbr.client_id
WHERE dbr.transaction_type=''Refund'' 
GROUP BY clientId,adjustmentDate,typeofAdjust ORDER BY clientId,adjustmentDate) ) t
WHERE (t.officeId= ''${officeId}'' or -1 = ''${officeId}'') AND t.adjustmentDate BETWEEN ''${startDate}''
 AND ''${endDate}'' ORDER BY t.clientId,t.adjustmentDate' WHERE report_name='Customer Adjustments Date Wise Details';
 

UPDATE stretchy_report SET report_sql='select
     off.name as BRANCH,
     clnt.id as `CLIENT ID`,
     clnt.display_name as `CLIENT NAME`,
     DATE_FORMAT(bdr.transaction_date,''%d-%m-%Y'') as `DEPOSIT DATE`,
    TRUNCATE(ifnull( bdr.debit_amount, 0),2) as `DEPOSIT`,
     DATE_FORMAT(pay.payment_date,''%d-%m-%Y'') as `COLLECTION DATE`,
     TRUNCATE((pay.amount_paid),2) as COLLECTION,
     DATE_FORMAT(bdr1.transaction_date,''%d-%m-%Y'')as `REFUND DATE`,
     TRUNCATE(bdr1.credit_amount,2) as `REFUND`            
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
  where (off.id = ''${officeId}'' or -1 = ''${officeId}'') 
  and  bdr.transaction_date between ''${startDate}'' and ''${endDate}''
 group by clnt.id,bdr.id
 order by off.office_type,clnt.id' WHERE report_name='Deposite And Refund Date wise Details';

UPDATE stretchy_report SET report_sql='SELECT
              clnt.account_no as `CUST.NO`,
			  cast(clnt.display_name as char charset utf8) as `CUST.NAME`,   
              DATE_FORMAT(inv.invoice_date, ''%d-%m-%Y'') as `INVOICE DATE`,
			 (CASE WHEN charge.priceline_id = 0 AND charge.charge_type = ''NRC'' THEN 
              btm.item_description ELSE pm.plan_description END) as ''PLAN/ITEM'',
              charge.charge_type as `CHARGE TYPE`,      
              cast(TRUNCATE(sum(charge.charge_amount),2) as char charset utf8) as `CHARGE AMOUNT`,
              cast(TRUNCATE(sum(charge.discount_amount),2) as char charset utf8) as `DISCOUNT AMOUNT`,
			  cast(TRUNCATE(sum(ctx.tax_amount),2) as char charset utf8) as `TAX AMOUNT`,
              cast(inv.invoice_amount as char charset utf8) as `INVOICE AMOUNT`
            
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
      LEFT JOIN 
       b_onetime_sale bos  ON charge.order_id = bos.id
	  LEFT JOIN 
      b_item_master btm  ON btm.id = bos.item_id
      LEFT JOIN 
      b_orders o ON o.id=charge.order_id and charge.client_id=clnt.id
      LEFT JOIN 
      b_plan_master pm ON  o.plan_id = pm.id 
      WHERE (off.id = ''${officeId}'' OR -1 = ''${officeId}'')  AND inv.invoice_date  BETWEEN ''${startDate}'' AND ''${endDate}''
      GROUP BY charge.invoice_id,inv.invoice_date,ctx.invoice_id order by clnt.id,inv.invoice_date'
      WHERE report_name='Invoice Date Wise Details';

UPDATE stretchy_report SET report_sql='
SELECT
             DATE_FORMAT(inv.invoice_date, ''%d-%m-%Y'') as `INVOICE DAY`,
              cast(TRUNCATE(sum(inv.invoice_amount),2) as char charset utf8) as `INVOICE AMOUNT`
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
 AND inv.invoice_date between ''${startDate}'' and ''${endDate}''
 GROUP BY inv.invoice_date' WHERE report_name='Invoice Day Wise Summary';

UPDATE stretchy_report SET report_sql='SELECT
            off.name as BRANCH,
            Year(inv.invoice_date) AS YEAR,
             MONTHNAME(inv.invoice_date) as MONTH,
            cast(TRUNCATE(sum(inv.invoice_amount),2) as char charset utf8) as AMOUNT

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
  GROUP BY MONTH' WHERE report_name='Invoice Month Wise Summary';


UPDATE stretchy_report SET report_sql=
'select 
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
    join b_payments pay ON clnt.id = pay.client_id
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
            cast(TRUNCATE(sum(ifnull(dbr.debit_amount, 0)), 2) as char) as collection
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


UPDATE stretchy_report SET report_sql='select distinct cb.client_id AS clientId
from
b_client_balance cb
join (select client_id,max(invoice_date) last_invoice_date,max(id) inv_id ,max(bill_id) bill_id
            from b_invoice where bill_id is not null group by client_id) bi
            on (cb.client_id = bi.client_id and balance_amount >0)
join b_orders bo on (cb.client_id = bo.client_id and order_status = 1 and is_deleted=''n'' )
left join (select distinct bill_id,plan_code  from b_bill_details where plan_code is not null
            and transaction_type !=''ONETIME_CHARGES'') bd
            on (bd.bill_id =bi.bill_id)
left join  (select client_id,max(payment_date) last_payment_date ,max(id) pmt_id
        from b_payments where is_deleted =0  group by client_id) bp
            on (cb.client_id = bp.client_id)
left join b_bill_master bm on (bd.bill_id = bm.id and bm.is_deleted = ''N'' )
where bo.plan_id = bd.plan_code
and   bi.bill_id = bd.bill_id
order by cb.client_id' WHERE report_name='UnpaidCustomers';

UPDATE job SET cron_expression='0 0 22 L * ?',cron_description='End of Month Last Day Night' WHERE name='STATEMENT';
UPDATE job SET cron_expression='0 0 23 L * ?',cron_description='End of Month Last Day Night' WHERE name='PDF';



