CREATE OR REPLACE view bMaster_vw_SN
AS 
select distinct `bm`.`id` AS `Bill_Id`,`bm`.`Bill_No` AS `Bill_No`,
`bm`.`Client_id` AS `Client_id`,cast(`bm`.`Bill_date` as date) AS `Bill_date`,
`bm`.`Bill_startdate` AS `Bill_startdate`,`bm`.`Bill_enddate` AS `Bill_enddate`,
`bm`.`Due_date` AS `Due_date`,`bm`.`Previous_balance` AS `Previous_balance`,
`bm`.`Charges_amount` AS `Charges_amount`,
`bm`.`Adjustment_amount` AS `Adjustment_amount`,
`bm`.`Tax_amount` AS `Tax_amount`,`bm`.`Paid_amount` AS `Paid_amount`,
`bm`.`Due_amount` AS `Due_amount`,
round((`bm`.`Tax_amount`),2) AS `vat` ,
round((`bm`.`Charges_amount` + `bm`.`Tax_amount`),2) bill_amt,
`bm`.`filename` AS `filename`,
`bm`.`Promotion_description` AS `Promotion_description`,
-- `bm`.`Bill_Period` AS `Bill_Period`,
-- `bm`.`adjustments_payments` AS `adjustments_payments`,
`ca`.`address_id` AS `address_id`,`ca`.`address_key` AS `address_key`,
`ca`.`address_no` AS `address_no`,`ca`.`street` AS `street`,
`ca`.`zip` AS `zip`,`ca`.`city` AS `city`,`ca`.`state` AS `state`,
`ca`.`country` AS `country`,
concat(`ca`.`address_no`,',',`ca`.`street`,',',`ca`.`city`,',',`ca`.`state`,
',',`ca`.`country`,'-',`ca`.`zip` ) as  address,
`ca`.`is_deleted` AS `is_deleted`,
`c`.`account_no` AS `account_no`,`c`.`external_id` AS `external_id`,
`c`.`status_enum` AS `status_enum`,`c`.`activation_date` AS `activation_date`,
`c`.`office_id` AS `office_id`,`c`.`firstname` AS `firstname`,
`c`.`middlename` AS `middlename`,`c`.`lastname` AS `lastname`,
`c`.`fullname` AS `fullname`,`c`.`display_name` AS `display_name`,
`c`.`image_key` AS `image_key`,`c`.`category_type` AS `category_type`,
round((`bm`.`Charges_amount` * 0.17),2) AS `excise_duty`,
sum(discount_amount) discount_amount
-- round((`bm`.`Tax_amount` - (`bm`.`Charges_amount` * 0.17)),2) AS `vat`
from  `b_bill_master` `bm` 
join `m_client` `c` on (`bm`.`Client_id` = `c`.`id`)
left join `b_client_address` `ca` on((`ca`.`client_id` = `c`.`id`)
 and address_key='PRIMARY' )
left join b_charge bc on (bm.id = bc.bill_id and discount_amount >0)
group by bm.id
;



CREATE or replace view bDetails_vw_sn
AS 
select distinct `bm`.`id` AS `Bill_Id`,`bm`.`Bill_No` AS `Bill_No`,
`bm`.`Client_id` AS `Client_id`,cast(`bm`.`Bill_date` as date) AS `Bill_date`,
`bm`.`Due_date` AS `Due_date`,`bm`.`Previous_balance` AS `Previous_balance`,
-- `bm`.`Charges_amount` AS `Charges_amount`,
sum(if((`bc`.`charge_type` = 'DC'),(0 - `bc`.`charge_amount`),`bc`.`charge_amount`)) AS `Charges_amount`,
-- sum(bc.charge_amount) AS `Charges_amount`,
`bm`.`Adjustment_amount` AS `Adjustment_amount`,
`bm`.`Tax_amount` AS `Tax_amount`,`bm`.`Paid_amount` AS `Paid_amount`,
`bm`.`Due_amount` AS `Due_amount`,`bm`.`filename` AS `filename`,
`bm`.`Promotion_description` AS `Promotion_description`,
concat(date(bc.charge_start_date),' - ',date(bc.charge_end_date)) AS `Bill_Period` ,
ifnull(`bpm`.`plan_description`,im.item_description) AS `plan_description`,
if((`bc`.`charge_type` = 'DC'),'Disc.Credit',
ifnull(`bo`.`billing_frequency`,'OneTime')) AS `billing_frequency`,
concat(cast(`bo`.`start_date` as date),' To ',
cast(`bo`.`end_date` as date)) AS `billDuration`,
round((`bm`.`Charges_amount` * 0.17),2) AS `excise_duty`,
round((`bm`.`Tax_amount`),2) AS `vat` ,
round((`bm`.`Charges_amount` + `bm`.`Tax_amount`),2) bill_amt
from `b_bill_master` `bm`
join b_charge bc on (bm.id = bc.bill_id and bc.charge_code in ('MSC','QSC','YSC','2MSC','OTC')) 
left join b_onetime_sale ots on (bc.order_id = ots.id and bc.charge_code='OTC')
left join b_item_master im on ( ots.item_id =im.id ) 
left join `b_orders` `bo` on ((`bm`.`client_id` = `bo`.`client_id`) 
	and bc.order_id = bo.id and bc.charge_code in ('MSC','QSC','YSC','2MSC','OTC'))
left join `b_plan_master` `bpm` on((`bo`.`plan_id` = `bpm`.`id`))
group by bc.bill_id, bc.charge_code 
;



CREATE OR REPLACE VIEW `billdetails_v` AS select `b`.`client_id` AS `client_id`,
`a`.`id` AS `transId`,`b`.`invoice_date` AS `transDate`,
'SERVICE_CHARGES' AS `transType`,if((`a`.`charge_type` = 'DC'),(0 - `a`.`netcharge_amount`),`a`.`netcharge_amount`) AS `amount`,
concat(date_format(`a`.`charge_start_date`,'%Y-%m-%d'),' to ',date_format(`a`.`charge_end_date`,'%Y-%m-%d')) AS `description`,
`c`.`plan_id` AS `plan_code`,`a`.`charge_type` AS `service_code` from ((`b_charge` `a` join `b_invoice` `b`) join `b_orders` `c`) where ((`a`.`invoice_id` = `b`.`id`) and 
(`a`.`order_id` = `c`.`id`) and isnull(`a`.`bill_id`) and (`b`.`invoice_date` <= now()) and (`a`.`priceline_id` <> 0) ) 
union all 
select `b`.`client_id` AS `client_id`,`a`.`id` AS `transId`,date_format(`b`.`invoice_date`,'%Y-%m-%d') AS `transDate`,
'TAXES' AS `transType`,if((`c`.`charge_type` = 'DC'),(0 - `a`.`tax_amount`),`a`.`tax_amount`) AS `amount`,
`a`.`tax_code` AS `description`,NULL AS `plan_code`,`c`.`charge_type` AS `service_code` from ((`b_charge_tax` `a` join `b_invoice` `b`) join `b_charge` `c`) 
where ((`a`.`invoice_id` = `b`.`id`) and (`a`.`charge_id` = `c`.`id`) and isnull(`a`.`bill_id`) and (`b`.`invoice_date` <= now()))
union all
 select `b_adjustments`.`client_id` AS `client_id`,`b_adjustments`.`id` AS `transId`,date_format(`b_adjustments`.`adjustment_date`,'%Y-%m-%d') AS `transDate`,
'ADJUSTMENT' AS `transType`,(case `b_adjustments`.`adjustment_type` when 'DEBIT' then `b_adjustments`.`adjustment_amount` when 'CREDIT' then -(`b_adjustments`.`adjustment_amount`) end) AS `amount`,
`b_adjustments`.`remarks` AS `remarks`,`b_adjustments`.`adjustment_type` AS `adjustment_type`,NULL AS `service_code` from `b_adjustments` where (isnull(`b_adjustments`.`bill_id`) and (`b_adjustments`.`adjustment_date` <= now())) 
union all 
select `pa`.`client_id` AS `client_id`,`pa`.`id` AS `transId`,date_format(`pa`.`payment_date`,'%Y-%m-%d') AS `transDate`,concat('PAYMENT',' - ',`p`.`code_value`) AS `transType`,`pa`.`amount_paid` AS `invoiceAmount`,`pa`.`Remarks` AS `remarks`,`p`.`code_value` AS `code_value`,NULL AS `service_code` from (`b_payments` `pa` join `m_code_value` `p`) where (isnull(`pa`.`bill_id`) and (`pa`.`payment_date` <= now()) and (`pa`.`paymode_id` = `p`.`id`)) 
union all 
select `b`.`client_id` AS `client_id`,`a`.`id` AS `transId`,date_format(`c`.`sale_date`,'%Y-%m-%d') AS `transDate`,'ONETIME_CHARGES' AS `transType`,`a`.`netcharge_amount` AS `amount`,`c`.`charge_code` AS `charge_code`,`c`.`item_id` AS `item_id`,`a`.`charge_type` AS `service_code` from ((`b_charge` `a` join `b_invoice` `b`) join `b_onetime_sale` `c`) where ((`a`.`invoice_id` = `b`.`id`) and (`a`.`order_id` = `c`.`id`) and isnull(`a`.`bill_id`) and (`c`.`sale_date` <= now()));






