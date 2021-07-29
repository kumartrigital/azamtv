

set @id =(select id from m_code where code_name='office Type');


insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'HO',0);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'LCO',3);

insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'DIST',4);

insert ignore into m_permission values(null,'organisation','SUSPEND_CLIENTSERVICE','CLIENTSERVICE','SUSPEND',0);

CREATE VIEW `office_fin_trans_vw` AS
select 
        `m_appuser`.`username` AS `username`,
        `b_itemsale`.`purchase_by` AS `office_id`,
        `m_invoice`.`id` AS `transId`,
        'Once' AS `tran_type`,
        cast(`m_invoice`.`invoice_date` as date) AS `transDate`,
        'INVOICE' AS `transType`,
        if((`m_invoice`.`invoice_amount` > 0),
            `m_invoice`.`invoice_amount`,
            0) AS `dr_amt`,
        if((`m_invoice`.`invoice_amount` < 0),
            abs(`m_invoice`.`invoice_amount`),
            0) AS `cr_amt`,
        1 AS `flag`
    from
        ((`m_invoice`
        join `m_appuser`)
        join `b_itemsale`)
    where
        ((`m_invoice`.`createdby_id` = `m_appuser`.`id`)
            and (`m_invoice`.`sale_id` = `b_itemsale`.`id`)
            and (`m_invoice`.`invoice_date` <= now())) 
    union all select 
        `m_appuser`.`username` AS `username`,
        `m_adjustments`.`office_id` AS `office_id`,
        `m_adjustments`.`id` AS `transId`,
        (select 
                `m_code_value`.`code_value`
            from
                `m_code_value`
            where
                ((`m_code_value`.`code_id` = 12)
                    and (`m_adjustments`.`adjustment_code` = `m_code_value`.`id`))) AS `tran_type`,
        cast(`m_adjustments`.`adjustment_date` as date) AS `transdate`,
        'ADJUSTMENT' AS `transType`,
        (case `m_adjustments`.`adjustment_type`
            when 'DEBIT' then `m_adjustments`.`adjustment_amount`
        end) AS `dr_amount`,
        0 AS `cr_amt`,
        1 AS `flag`
    from
        (`m_adjustments`
        join `m_appuser`)
    where
        ((`m_adjustments`.`adjustment_date` <= now())
            and (`m_adjustments`.`adjustment_type` = 'DEBIT')
            and (`m_adjustments`.`createdby_id` = `m_appuser`.`id`)) 
    union all select 
        `m_appuser`.`username` AS `username`,
        `m_payments`.`office_id` AS `office_id`,
        `m_payments`.`id` AS `transId`,
        (select 
                `m_code_value`.`code_value`
            from
                `m_code_value`
            where
                ((`m_code_value`.`code_id` = 11)
                    and (`m_payments`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,
        cast(`m_payments`.`payment_date` as date) AS `transDate`,
        'PAYMENT' AS `transType`,
        0 AS `dr_amt`,
        `m_payments`.`amount_paid` AS `cr_amount`,
        `m_payments`.`is_deleted` AS `flag`
    from
        (`m_payments`
        join `m_appuser`)
    where
        ((`m_payments`.`createdby_id` = `m_appuser`.`id`)
            and (`m_payments`.`payment_date` <= now()))
    order by 1 , 2;
