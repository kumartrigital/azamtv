DROP FUNCTION IF EXISTS sms_conf;
DELIMITER //
CREATE  FUNCTION sms_conf(message_id int)
  RETURNS TEXT
  LANGUAGE SQL
BEGIN
  DECLARE smsto VARCHAR(10);
  DECLARE smstext VARCHAR(255);
  DECLARE postdata VARCHAR(255);
  SET @smsto = '';
  SET @smstext = '';
  SET @postdata = '';
select message_to,body into @smsto,@smstext from b_message_data where id=message_id;
  SET @postdata = CONCAT('User=venkat&passwd=venkat@123&mobilenumber=',@smsto,'&message=',@smstext,'&sid=xxxxxxxx&mtype=N&DR=Y');
  RETURN @postdata;
END //
DELIMITER;

insert IGNORE into c_configuration (id,name,enabled,value) VALUES (null,'align-biiling-cycle',0,0);
alter table b_charge_tax modify `tax_value` BIGINT(10) NOT NULL;
insert IGNORE into m_permission values(null, 'billing', 'UPDATE_STATICIP', 'STATICIP', 'UPDATE', '0');

-- Statement generation --
CREATE OR REPLACE VIEW `billdetails_v` AS 
select 
    b.client_id AS client_id,
    a.id AS transId,
    b.invoice_date AS transDate,
    'SERVICE_CHARGES' AS transType,
   -- if((a.charge_type = 'DC'),
     --   (0 - a.netcharge_amount),
        a.netcharge_amount AS amount,
    concat(date_format(a.charge_start_date, '%Y-%m-%d'),
            ' to ',
            date_format(a.charge_end_date, '%Y-%m-%d')) AS description,
    c.plan_id AS plan_code,
    a.charge_type AS service_code
from
    ((b_charge a
    join b_invoice b)
    join b_orders c)
where
    ((a.invoice_id = b.id) and (a.order_id = c.id) and isnull(a.bill_id) and (b.invoice_date <= now()) and (a.priceline_id <> 0)) 
union all select 
    b.client_id AS client_id,
    a.id AS transId,
    date_format(b.invoice_date, '%Y-%m-%d') AS transDate,
    'TAXES' AS transType,
   -- if(((c.charge_type = 'DC') or (a.tax_value = 1)),
    --    (0 - a.tax_amount),
        a.tax_amount AS amount,
    a.tax_code AS description,
    NULL AS plan_code,
    c.charge_type AS service_code
from
    ((b_charge_tax a
    join b_invoice b)
    join b_charge c)
where
    ((a.invoice_id = b.id) and (a.charge_id = c.id) and isnull(a.bill_id) and (b.invoice_date <= now())) 
union all select 
    b_adjustments.client_id AS client_id,
    b_adjustments.id AS transId,
    date_format(b_adjustments.adjustment_date,
            '%Y-%m-%d') AS transDate,
    'ADJUSTMENT' AS transType,
    (case b_adjustments.adjustment_type
        when 'DEBIT' then b_adjustments.adjustment_amount
        when 'CREDIT' then -(b_adjustments.adjustment_amount)
    end) AS amount,
    b_adjustments.remarks AS remarks,
    b_adjustments.adjustment_type AS adjustment_type,
    NULL AS service_code
from
    b_adjustments
where
    (isnull(b_adjustments.bill_id) and (b_adjustments.adjustment_date <= now())) 
union all select 
    pa.client_id AS client_id,
    pa.id AS transId,
    date_format(pa.payment_date, '%Y-%m-%d') AS transDate,
    concat('PAYMENT', ' - ', p.code_value) AS transType,
    pa.amount_paid AS invoiceAmount,
    pa.Remarks AS remarks,
    p.code_value AS code_value,
    NULL AS service_code
from
    (b_payments pa
    join m_code_value p)
where
    (isnull(pa.bill_id) and (pa.payment_date <= now()) and (pa.paymode_id = p.id)) 
union all select 
    b.client_id AS client_id,
    a.id AS transId,
    date_format(c.sale_date, '%Y-%m-%d') AS transDate,
    'ONETIME_CHARGES' AS transType,
    a.netcharge_amount AS amount,
    c.charge_code AS charge_code,
    c.item_id AS item_id,
    a.charge_type AS service_code
from
    ((b_charge a
    join b_invoice b)
    join b_onetime_sale c)
where
    ((a.invoice_id = b.id) and (a.order_id = c.id) and isnull(a.bill_id) and (c.sale_date <= now()) and (c.invoice_id = b.id) and (a.priceline_id = 0))
union all select 
    b.client_id AS client_id,
    a.id AS transId,
    b.invoice_date AS transDate,
    'SERVICE_TRANSFER' AS transType,
     a.netcharge_amount AS amount,
    concat(date_format(a.charge_start_date, '%Y-%m-%d'),
            ' to ',
            date_format(a.charge_end_date, '%Y-%m-%d')) AS description,
    ph.property_code AS plan_code,
    a.charge_type AS service_code
from
    ((b_charge a
    join b_invoice b)
    join b_property_history ph)
where
    ((a.invoice_id = b.id) and (a.order_id = ph.id) 
and isnull(a.bill_id) and (b.invoice_date <= now()) and (a.priceline_id = -1)) ;




