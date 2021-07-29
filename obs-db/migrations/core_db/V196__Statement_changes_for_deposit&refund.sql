DROP procedure IF EXISTS billIdInStatement; 
DELIMITER //
CREATE procedure billIdInStatement() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='bill_id'
     and TABLE_NAME ='b_deposit_refund'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_deposit_refund ADD COLUMN bill_id INT(20) DEFAULT NULL AFTER is_refund;
END IF;
END //
DELIMITER ;
call billIdInStatement();
DROP procedure IF EXISTS billIdInStatement;


DROP procedure IF EXISTS depositInStatement; 
DELIMITER //
CREATE procedure depositInStatement() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='deposit_refund_amount'
     and TABLE_NAME ='b_bill_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_bill_master ADD COLUMN deposit_refund_amount DOUBLE(24,4) DEFAULT NULL AFTER paid_amount;
END IF;
END //
DELIMITER ;
call depositInStatement();
DROP procedure IF EXISTS depositInStatement;

CREATE OR REPLACE VIEW  billdetails_v
 AS
    select 
        `b`.`client_id` AS `client_id`,
        `a`.`id` AS `transId`,
        `b`.`invoice_date` AS `transDate`,
        'SERVICE_CHARGES' AS `transType`,
        `a`.`netcharge_amount` AS `amount`,
        concat(date_format(`a`.`charge_start_date`, '%Y-%m-%d'),
                ' to ',
                date_format(`a`.`charge_end_date`, '%Y-%m-%d')) AS `description`,
        `c`.`plan_id` AS `plan_code`,
        `a`.`charge_type` AS `service_code`
    from
        ((`b_charge` `a`
        join `b_invoice` `b`)
        join `b_orders` `c`)
    where
        ((`a`.`invoice_id` = `b`.`id`)
            and (`a`.`order_id` = `c`.`id`)
            and isnull(`a`.`bill_id`)
            and (`b`.`invoice_date` <= now())
            and (`a`.`priceline_id` >= 1)) 
    union all select 
        `b`.`client_id` AS `client_id`,
        `a`.`id` AS `transId`,
        date_format(`b`.`invoice_date`, '%Y-%m-%d') AS `transDate`,
        'TAXES' AS `transType`,
        `a`.`tax_amount` AS `amount`,
        `a`.`tax_code` AS `description`,
        NULL AS `plan_code`,
        `c`.`charge_type` AS `service_code`
    from
        ((`b_charge_tax` `a`
        join `b_invoice` `b`)
        join `b_charge` `c`)
    where
        ((`a`.`invoice_id` = `b`.`id`)
            and (`a`.`charge_id` = `c`.`id`)
            and isnull(`a`.`bill_id`)
            and (`b`.`invoice_date` <= now())) 
    union all select 
        `b_adjustments`.`client_id` AS `client_id`,
        `b_adjustments`.`id` AS `transId`,
        date_format(`b_adjustments`.`adjustment_date`,
                '%Y-%m-%d') AS `transDate`,
        'ADJUSTMENT' AS `transType`,
        (case `b_adjustments`.`adjustment_type`
            when 'DEBIT' then -(`b_adjustments`.`adjustment_amount`)
            when 'CREDIT' then `b_adjustments`.`adjustment_amount`
        end) AS `amount`,
        `b_adjustments`.`remarks` AS `remarks`,
        `b_adjustments`.`adjustment_type` AS `adjustment_type`,
        NULL AS `service_code`
    from
        `b_adjustments`
    where
        (isnull(`b_adjustments`.`bill_id`)
            and (`b_adjustments`.`adjustment_date` <= now())) 
    union all select 
        `pa`.`client_id` AS `client_id`,
        `pa`.`id` AS `transId`,
        date_format(`pa`.`payment_date`, '%Y-%m-%d') AS `transDate`,
        concat('PAYMENT', ' - ', `p`.`code_value`) AS `transType`,
        `pa`.`amount_paid` AS `invoiceAmount`,
        `pa`.`Remarks` AS `remarks`,
        `p`.`code_value` AS `code_value`,
        NULL AS `service_code`
    from
        (`b_payments` `pa`
        join `m_code_value` `p`)
    where
        (isnull(`pa`.`bill_id`)
            and (`pa`.`payment_date` <= now())
            and (`pa`.`paymode_id` = `p`.`id`)) 
    union all select 
        `b`.`client_id` AS `client_id`,
        `a`.`id` AS `transId`,
        date_format(`c`.`sale_date`, '%Y-%m-%d') AS `transDate`,
        'ONETIME_CHARGES' AS `transType`,
        `a`.`netcharge_amount` AS `amount`,
        `c`.`charge_code` AS `charge_code`,
        `c`.`item_id` AS `item_id`,
        `a`.`charge_type` AS `service_code`
    from
        ((`b_charge` `a`
        join `b_invoice` `b`)
        join `b_onetime_sale` `c`)
    where
        ((`a`.`invoice_id` = `b`.`id`)
            and (`a`.`order_id` = `c`.`id`)
            and isnull(`a`.`bill_id`)
            and (`c`.`sale_date` <= now())
            and (`c`.`invoice_id` = `b`.`id`)
            and (`a`.`priceline_id` = 0)) 
    union all select 
        `b`.`client_id` AS `client_id`,
        `a`.`id` AS `transId`,
        `b`.`invoice_date` AS `transDate`,
        'SERVICE_TRANSFER' AS `transType`,
        `a`.`netcharge_amount` AS `amount`,
        concat(date_format(`a`.`charge_start_date`, '%Y-%m-%d'),
                ' to ',
                date_format(`a`.`charge_end_date`, '%Y-%m-%d')) AS `description`,
        `ph`.`property_code` AS `plan_code`,
        `a`.`charge_type` AS `service_code`
    from
        ((`b_charge` `a`
        join `b_invoice` `b`)
        join `b_property_history` `ph`)
    where
        ((`a`.`invoice_id` = `b`.`id`)
            and (`a`.`order_id` = `ph`.`id`)
            and isnull(`a`.`bill_id`)
            and (`b`.`invoice_date` <= now())
            and (`a`.`priceline_id` = -(1))) 
    union all select 
        `bdr`.`client_id` AS `client_id`,
        `bdr`.`id` AS `transId`,
        date_format(`bdr`.`transaction_date`, '%Y-%m-%d') AS `transDate`,
        'DEPOSIT&REFUND' AS `transType`,
        (case
            when (`bdr`.`description` in ('Deposit' , 'Payment Towards Refund Entry')) then `bdr`.`debit_amount`
            when (`bdr`.`description` in ('Refund' , 'Refund Adjustment towards Service Balance')) then -(`bdr`.`credit_amount`)
        end) AS `amount`,
        `bdr`.`description` AS `remarks`,
        `bdr`.`transaction_type` AS `transaction`,
        NULL AS `service_code`
    from
        `b_deposit_refund` `bdr`
    where
        ((`bdr`.`transaction_date` <= now())
            and isnull(`bdr`.`bill_id`));



CREATE 
    OR REPLACE
VIEW `bmaster_vw` AS
    select distinct
        `bm`.`id` AS `billId`,
        `bm`.`Bill_No` AS `billNo`,
        `bm`.`Client_id` AS `clientId`,
        cast(`bm`.`Bill_date` as date) AS `billDate`,
        `bm`.`Due_date` AS `dueDate`,
        `bm`.`Previous_balance` AS `previousBalance`,
        `bm`.`Charges_amount` AS `chargesAmount`,
        `bm`.`Adjustment_amount` AS `adjustmentAmount`,
        round(`bm`.`Tax_amount`, 2) AS `taxAmount`,
        `bm`.`Paid_amount` AS `paidAmount`,
        `bm`.`deposit_refund_amount` AS `depositRefundAmount`,
        `bm`.`Due_amount` AS `dueAmount`,
        `bm`.`Promotion_description` AS `Description`,
        sum(`bc`.`discount_amount`) AS `discountAmount`,
        `ca`.`address_id` AS `addressId`,
        `ca`.`address_key` AS `addressKey`,
        `ca`.`address_no` AS `addressNo`,
        `ca`.`street` AS `street`,
        `ca`.`zip` AS `zip`,
        `ca`.`city` AS `city`,
        `ca`.`state` AS `state`,
        `ca`.`country` AS `country`,
        concat(`ca`.`address_no`,
                `ca`.`street`,
                ',',
                `ca`.`city`,
                ',',
                `ca`.`state`,
                ',',
                `ca`.`country`,
                '-',
                `ca`.`zip`) AS `address`,
        `c`.`account_no` AS `accountNo`,
        `c`.`external_id` AS `externalId`,
        `c`.`status_enum` AS `status`,
        `c`.`activation_date` AS `activationDate`,
        `c`.`office_id` AS `officeId`,
        `c`.`firstname` AS `firstname`,
        `c`.`middlename` AS `middlename`,
        `c`.`lastname` AS `lastname`,
        `c`.`display_name` AS `displayName`,
        `c`.`email` AS `emailId`,
        `c`.`image_key` AS `imageKey`,
        `c`.`category_type` AS `categoryType`,
        `mo`.`name` AS `officeName`
    from
        ((((`b_bill_master` `bm`
        join `m_client` `c` ON ((`bm`.`Client_id` = `c`.`id`)))
        join `m_office` `mo` ON ((`c`.`office_id` = `mo`.`id`)))
        left join `b_client_address` `ca` ON (((`ca`.`client_id` = `c`.`id`)
            and (`ca`.`address_key` = 'PRIMARY')
            and (`ca`.`is_deleted` = 'n'))))
        left join `b_charge` `bc` ON (((`bm`.`id` = `bc`.`bill_id`)
            and (`bc`.`discount_amount` > 0))))
    group by `bm`.`id`
