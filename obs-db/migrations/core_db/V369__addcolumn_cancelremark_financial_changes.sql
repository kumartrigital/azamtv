Drop procedure IF EXISTS cancelremark;
DELIMITER //
create procedure cancelremark() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'cancel_remark'
     and TABLE_NAME = 'm_payments'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_payments`
ADD COLUMN `cancel_remark` VARCHAR(100) NULL DEFAULT NULL AFTER `is_wallet`;
END IF;
END //
DELIMITER ;
call cancelremark();
Drop procedure IF EXISTS cancelremark;



INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('organisation', 'DELETE_OFFICEPAYMENT', 'OFFICEPAYMENT', 'DELETE', '1');



drop view if exists office_fin_trans_vw;
CREATE  VIEW  `office_fin_trans_vw` AS
    SELECT 
        `m_appuser`.`username` AS `username`,
        `b_itemsale`.`purchase_by` AS `office_id`,
        `m_invoice`.`id` AS `transId`,
        'Once' AS `tran_type`,
        CAST(`m_invoice`.`invoice_date` AS DATE) AS `transDate`,
        'INVOICE' AS `transType`,
        IF((`m_invoice`.`invoice_amount` > 0),
            `m_invoice`.`invoice_amount`,
            0) AS `dr_amt`,
        IF((`m_invoice`.`invoice_amount` < 0),
            ABS(`m_invoice`.`invoice_amount`),
            0) AS `cr_amt`,
        1 AS `flag`,
        0 as receiptid,
        `m_office`.`po_id` as office_poid,
        'None' as cancelRemark,
        'Y' as isDeleted
    FROM
        ((`m_invoice`
        JOIN `m_appuser`)
        JOIN `b_itemsale`
        JOIN `m_office`)
    WHERE
        ((`m_invoice`.`createdby_id` = `m_appuser`.`id`)
            AND (`m_invoice`.`sale_id` = `b_itemsale`.`id`)
            AND (`b_itemsale`.`purchase_by` = `m_office`.`id`)
            AND (`m_invoice`.`invoice_date` <= NOW())) 
    UNION ALL SELECT 
        `m_appuser`.`username` AS `username`,
        `m_adjustments`.`office_id` AS `office_id`,
        `m_adjustments`.`id` AS `transId`,
        (SELECT 
                `m_code_value`.`code_value`
            FROM
                `m_code_value`
            WHERE
                ((`m_code_value`.`code_id` = 12)
                    AND (`m_adjustments`.`adjustment_code` = `m_code_value`.`id`))) AS `tran_type`,
        CAST(`m_adjustments`.`adjustment_date` AS DATE) AS `transdate`,
        'ADJUSTMENT' AS `transType`,
        (CASE `m_adjustments`.`adjustment_type`
            WHEN 'DEBIT' THEN `m_adjustments`.`adjustment_amount`
        END) AS `dr_amount`,
        0 AS `cr_amt`,
        1 AS `flag`,
        0 as receiptid,
        `m_office`.`po_id` as office_poid,
        'None' as cancelRemark,
        'Y' as isDeleted
    FROM
        (`m_adjustments`
        JOIN `m_appuser`
        JOIN `m_office`)
    WHERE
        ((`m_adjustments`.`adjustment_date` <= NOW())
            AND (`m_adjustments`.`adjustment_type` = 'DEBIT')
            AND (`m_adjustments`.`office_id` = `m_office`.`id`)
            AND (`m_adjustments`.`createdby_id` = `m_appuser`.`id`)) 
    UNION ALL SELECT 
        `m_appuser`.`username` AS `username`,
        `m_payments`.`office_id` AS `office_id`,
        `m_payments`.`id` AS `transId`,
        (SELECT 
                `m_code_value`.`code_value`
            FROM
                `m_code_value`
            WHERE
                ((`m_code_value`.`code_id` = 11)
                    AND (`m_payments`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,
        CAST(`m_payments`.`payment_date` AS DATE) AS `transDate`,
        'PAYMENT' AS `transType`,
        0 AS `dr_amt`,
        `m_payments`.`amount_paid` AS `cr_amount`,
        `m_payments`.`is_deleted` AS `flag`,
        `m_payments`.`receipt_no` as receiptid,
        `m_office`.`po_id` as office_poid,
        `m_payments`.`cancel_remark` as cancelRemark,
        `m_payments`.`is_deleted` as isDeleted
    FROM
        (`m_payments`
        JOIN `m_appuser`
        JOIN `m_office`)
    WHERE
        ((`m_payments`.`createdby_id` = `m_appuser`.`id`)
        AND (`m_payments`.`office_id` = `m_office`.`id`)
            AND (`m_payments`.`payment_date` <= NOW()));
