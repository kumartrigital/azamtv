-- city wise orders --
CREATE 
   OR REPLACE
VIEW `city_order_plan_vw` AS
    select 
        `ca`.`country` AS `COUNTRY`,
        `ca`.`city` AS `CITY`,
        `of`.`name` AS `BRANCH`,
        `c`.`display_name` AS `CLIENT NAME`,
        `pm`.`plan_description` AS `PLAN NAME`,
        `o`.`order_no` AS `ORDER NO`,
        `ev`.`enum_value` AS `ORDER STATUS`,
        `bcp`.`contract_period` AS `CONTRACT PERIOD`,
        cast(`o`.`start_date` as date) AS `START DATE`,
        cast(`o`.`end_date` as date) AS `END DATE`,
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

-- list of disconnections  --
CREATE 
    OR REPLACE
VIEW `discon_vw` AS
    select distinct
        `c`.`office_id` AS `OFFICEID`,
        `mo`.`name` AS `BRANCH`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `c`.`phone` AS `PHONE NO`,
        `pm`.`plan_description` AS `PLAN`,
        `bcp`.`contract_period` AS `CONTRACT PERIOD`,
        `o`.`billing_frequency` AS `BILL FRQUENCY`,
        cast(`c`.`activation_date` as date) AS `ACTIVATION DATE`,
        cast(`oh`.`actual_date` as date) AS `DISCONNECTION DATE`,
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

-- List of hardware allocations --

CREATE 
   OR REPLACE
VIEW `hw_alloc_vw` AS
    select distinct
        `c`.`office_id` AS `Office Id`,
        `mo`.`name` AS `Office Name`,
        `c`.`id` AS `Client Id`,
        `c`.`display_name` AS `Client Name`,
        `im`.`item_code` AS `Item Code`,
        `im`.`item_description` AS `Description`,
        `a`.`serial_no` AS `SerialNo`,
        `id`.`provisioning_serialno` AS `ProvisioningNo`,
        cast(`a`.`allocation_date` as date) AS `Allocation Date`,
        `bos`.`sale_date` AS `Sale Date`,
        `im`.`unit_price` AS `Sale Price`,
        `id`.`status` AS `Status`,
        `im`.`warranty` AS `Warranty`,
        cast(`id`.`warranty_date` as date) AS `WarrantyExpiry Date`
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

SET SQL_SAFE_UPDATES=0;

update stretchy_report
SET report_sql='select * FROM hw_alloc_vw  where `Allocation Date`  
between ''${startDate}'' and ''${endDate}'' and  (Office Id = ''${officeId}'' or -1 = ''${officeId}'')'
where report_name='List of HardWare Allocations';


-- list of cancelled -- 
CREATE 
    OR REPLACE
VIEW `cancelled_vw` AS
    select distinct
        cast(`oh`.`actual_date` as date) AS `CANCELLATION DATE`,
        cast(`c`.`activation_date` as date) AS `ACTIVATION DATE`,
        (to_days(`oh`.`actual_date`) - to_days(`c`.`activation_date`)) AS `ACTIVE DAYS`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `ca`.`city` AS `CITY`,
        `ca`.`state` AS `STATE`,
        `ca`.`country` AS `COUNTRY`,
        `pm`.`plan_description` AS `PLAN`,
        `o`.`billing_frequency` AS `BILL FREQUENCY`,
        format(`op`.`price`, 2) AS `PRICE`
    from
        (((((`m_client` `c`
        join `b_client_address` `ca` ON ((`c`.`id` = `ca`.`client_id`)))
        join `b_orders` `o` ON ((`c`.`id` = `o`.`client_id`)))
        join `b_orders_history` `oh` ON ((`o`.`id` = `oh`.`order_id`)))
        left join `b_plan_master` `pm` ON ((`o`.`plan_id` = `pm`.`id`)))
        left join `b_order_price` `op` ON ((`o`.`id` = `op`.`order_id`)))
    where
        (`oh`.`transaction_type` = 'Cancelled');

-- list of Device sales --
SET @offId=(SELECT id FROM stretchy_parameter where parameter_label='Office');
SET @ID=(SELECT id FROM stretchy_report where report_name='List of Device Sales');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@ID,@offId,'Office');

UPDATE stretchy_report
SET report_sql='SELECT 
    o.name AS Branch,
    a.account_no AS AccountNo,
    CASE
	    WHEN status_enum = 100 THEN ''New''
        WHEN status_enum = 300 THEN ''Active''
        WHEN status_enum = 600 THEN ''Deactive''
        ELSE status_enum
    END as ClientStatus,
    a.display_name AS ClientName,
    im.item_description AS Device,
    DATE_FORMAT(b.sale_date, ''%Y-%m-%d'') SaleDate,
    FORMAT(b.total_price,2) AS SalePrice
FROM
     m_office o join m_client a on  o.id=a.office_id
     join b_onetime_sale b on  b.client_id = a.id
     join  b_item_master im on b.item_id = im.id
WHERE
 o.id= ''${officeId}''  or -1 = ''${officeId}''  and b.sale_date between ''${startDate}'' and ''${endDate}''
order by b.sale_date' where report_name='List of Device Sales';



-- plan wise service --
UPDATE stretchy_report
SET report_sql='select 
    plan.plan_description As `Plan Description`,
    plan.service_code as `Service Code`,
    plan.enum_value as `Status`,
    plan.charge_code as `Charge Code`,
    plan.charge_type as `Charge type`,
    plan.active_since as `Active Since`,
    format(plan.price,2) as `Price`
from
    (SELECT 
           plnmstr.id as plan_id,
            plnmstr.plan_description,
            plnprc.service_code,
            ev.enum_value,
            plnprc.charge_code,
            chrgcds.charge_type,
            concat(chrgcds.charge_duration, ' ', chrgcds.duration_type) as Active_Since,
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
order by plan.plan_id , plan.service_code , plan.charge_code' where report_name='Plan wise services';


SET @planID=(SELECT id FROM stretchy_parameter where parameter_label='Plan Name');
SET @DATEID=(SELECT id FROM stretchy_parameter where parameter_label='StartDate');
SET @DID=(SELECT id FROM stretchy_parameter where parameter_label='EndDate');

-- Plan Wise Revenue DayWise Summary--
insert ignore into stretchy_report values(Null, 'Plan Wise Revenue DayWise Summary', 'Table', '', 'Invoice&Collections', 'SELECT 
    plnmst.plan_description planName,
    DATE_FORMAT(invc.Invoice_date, ''%Y-%m-%d'') as invoiceDate,
    cast(sum(round(chrg.charge_amount, 2)) as char) chargeAmount,
    cast(sum(round(chrg.discount_amount, 2)) as char) discountAmount,
    cast(sum(round(ifnull(tax.Tax_amount, 0), 2)) as char) taxAmount,
    cast(SUM(round(invc.invoice_amount, 2)) as char) invoiceAmount
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
GROUP BY invoiceDate,planName
order by invoiceDate', 'Plan Wise Revenue DayWise Summary of Clients', '0', '1');


SET @id=(SELECT id FROM stretchy_report where report_name='Plan Wise Revenue DayWise Summary');
insert ignore into stretchy_report_parameter (report_id,parameter_id,report_parameter_name) values (@id,@planID,'Plan Name');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DATEID,'StartDate');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DID,'EndDate');

-- Plan Wise Revenue DayWise chart insert query --
insert ignore into stretchy_report values(Null, 'Plan Wise Revenue DayWise chart', 'Chart', 'pie', 'Invoice&Collections', 'SELECT 
    concat(cast(DATE_FORMAT(invc.Invoice_date, ''%Y-%m-%d'') as char),'' - '',cast(plnmst.plan_description as char )) as Date,
    cast(SUM(invc.invoice_amount) as char)
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
GROUP BY invc.Invoice_date,plnmst.plan_description
order by invc.Invoice_date', 'Plan Wise Revenue DayWise chart of Clients', '1', '1');

SET @id=(SELECT id FROM stretchy_report where report_name='Plan Wise Revenue DayWise chart');
insert ignore into stretchy_report_parameter (report_id,parameter_id,report_parameter_name) values (@id,@planID,'Plan Name');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DATEID,'StartDate');
insert ignore into stretchy_report_parameter(report_id,parameter_id,report_parameter_name)values (@id,@DID,'EndDate');

-- Plan Wise Revenue MonthWise chart --
insert ignore into stretchy_report values(Null, 'Plan Wise Revenue MonthWise chart', 'Chart', 'pie', 'Invoice&Collections', 'SELECT 
    concat(Monthname(invc.Invoice_date),'' - '',cast(plnmst.plan_description as char )) as Month,
    cast(SUM(invc.invoice_amount) as char)
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
  (plnmst.id = ''${planId}'' or - 1 = ''{planId}'')
GROUP BY Plan_Name,Month
order by Month', 'Plan Wise Revenue MonthWise chart of clients', '1', '1');

-- Plan Wise Revenue MonthWise chart update query --

  UPDATE stretchy_report
  SET report_sql ='SELECT 
    concat(Monthname(invc.Invoice_date),'' - '',cast(plnmst.plan_description as char )) as Month,
    cast(SUM(invc.invoice_amount) as char)
  from
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
GROUP BY Monthname(invc.Invoice_date),plnmst.plan_description
order by Monthname(invc.Invoice_date)'
where report_name='Plan Wise Revenue MonthWise chart';

-- Plan Wise Revenue MonthWise chart parameters --

SET @id=(SELECT id FROM stretchy_report where report_name='Plan Wise Revenue MonthWise chart');
SET @planID=(SELECT id FROM stretchy_parameter where parameter_label='Plan Name');
insert ignore into stretchy_report_parameter (report_id,parameter_id,report_parameter_name) values (@id,@planID,'Plan Name');

-- Plan wise Revenue MonthWise Details update query --

UPDATE stretchy_report set report_name='Plan Wise Revenue MonthWise Details' where report_name='Plan_wise_Revenue_MonthWise';

UPDATE stretchy_report SET report_sql ='SELECT 
    plnmst.plan_description planName,
    Monthname(invc.Invoice_date) AS Month,
    cast(round(sum(chrg.charge_amount), 2) as char) ChargeAmount,
    cast(round(sum(chrg.discount_amount), 2) as char) DiscountAmount,
    cast(round(sum(ifnull(tax.Tax_amount, 0)), 2) as char) TaxAmount,
    cast(round((sum(chrg.charge_amount) - sum(chrg.discount_amount) + sum(ifnull(tax.Tax_amount, 0))),2)
        as char) AS InvoiceAmount
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
GROUP BY Monthname(invc.Invoice_date), plnmst.id'
where report_name='Plan Wise Revenue MonthWise Details';


-- Plan Wise Revenue Date Wise Details --

UPDATE stretchy_report set report_name='Plan Wise Revenue Date Wise Details' where report_name='Plan_Wise_Revenue_Detail';

UPDATE stretchy_report
SET report_sql='SELECT plnmst.plan_description planName,
invc.id invoiceNo,
DATE_FORMAT(invc.Invoice_date,''%Y-%m-%d'') as invoiceDate,
chrg.client_id clientId,
clnt.display_name clientName,
DATE_FORMAT(chrg.charge_start_date,''%Y-%m-%d'') as chargeFrom,
DATE_FORMAT(chrg.charge_end_date,''%Y-%m-%d'') as chargeTo,
cast(round(chrg.charge_amount,2) as char) chargeAmount,
cast(round(chrg.discount_amount,2)as char) discountAmount,
cast(round(ifnull(tax.Tax_amount,0),2) as char)  taxAmount,
cast(round(invc.invoice_amount,2) as char) invoiceAmount 
FROM b_plan_master plnmst 
JOIN b_orders ord ON plnmst.id = ord.plan_id 
JOIN b_charge chrg ON chrg.order_id = ord.id 
JOIN b_invoice invc ON invc.client_id = chrg.client_id AND invc.id = chrg.invoice_id AND charge_type = ''RC'' 
JOIN m_client clnt ON clnt.id = invc.client_id 
LEFT OUTER JOIN b_charge_tax tax ON tax.charge_id = chrg.id 
WHERE  (plnmst.id = ''${planId}'' or -1 = ''${planId}'') and invc.Invoice_date between ''${startDate}'' and ''${endDate}''
GROUP BY plnmst.id,invc.id ORDER BY invc.id'
where report_name='Plan Wise Revenue Date Wise Details';


