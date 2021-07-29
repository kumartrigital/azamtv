SET SQL_SAFE_UPDATES=0;
DELETE IGNORE FROM stretchy_report where report_name='Agent Commission Report';

DROP PROCEDURE IF EXISTS `proc_office_commission`;
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_office_commission`()
BEGIN
insert into b_office_commission
select null id,bc.id charge_id,
mc.office_id,bi.invoice_date,
oad.source,oad.share_amount,oad.share_type ,mcv.code_value comm_source,
case 
when oad.share_type='Flat' and bc.charge_type='NRC' then round(oad.share_amount,2)
when oad.share_type='Percentage' and bc.charge_type='NRC' then round(bc.netcharge_amount * oad.share_amount / 100,2) 
when oad.share_type='Percentage' and bc.charge_type <>'NRC' then round(bc.netcharge_amount * oad.share_amount / 100,2) 
else round(oad.share_amount,2) end amt ,now() created_dt
from b_charge bc
join b_invoice bi on (bc.invoice_id=bi.id)
join m_client mc on (bc.client_id=mc.id)
join m_office_agreement oa on (oa.office_id = mc.office_id)
join m_office_agreement_detail oad on (oad.agreement_id = oa.id and oad.is_deleted='N'
 and oad.source = 
 case when bc.charge_type ='NRC' then (select id from m_code_value cv where cv.code_value='Hardware') 
 when bc.charge_type ='RC' then (select id from m_code_value cv where cv.code_value='Subscriptions')  else (select id from m_code_value cv where cv.code_value='On-demand') end)
join m_code_value mcv on (mcv.id = oad.source and mcv.code_id=(select id from m_code mc where mc.code_name='Source Category'))
where 
bc.id not in 
 (select charge_id from b_office_commission)
and bc.id >= 
 ifnull( (select max(charge_id) from b_office_commission),0);
END $$
DELIMITER ;


CREATE OR REPLACE VIEW `v_agent_commission` AS
    select 
        `r`.`id` AS `RESELLER ID`,
        `r`.`name` AS `RESELLER NAME`,
        `bc`.`client_id` AS `CLIENT ID`,
        `mc`.`display_name` AS `CLIENT NAME`,
        `bca`.`country` AS `COUNTRY`,
        cast(`boc`.`invoice_date` as date) AS `INVOICE DATE`,
        date_format(`boc`.`invoice_date`, '%b-%Y') AS `MONTH`,
        coalesce(`bpm`.`plan_description`,`im`.`item_description`) AS `PLAN NAME`,
        `mcv`.`code_value` AS `SOURCE`,
        cast(round(`bc`.`netcharge_amount`, 2) as char charset utf8) AS `CHARGE AMOUNT`,
        `boc`.`share_amount` AS `SHARE AMOUNT`,
        `boc`.`share_type` AS `SHARE TYPE`,
        cast(round(`boc`.`amt`, 2) as char charset utf8) AS `COMM AMOUNT`
    from
        ((((((((((`b_office_commission` `boc`
        left join `m_code_value` `mcv` ON ((`mcv`.`id` = `boc`.`source`)))
        join `m_office` `r` ON ((`boc`.`office_id` = `r`.`id`)))
        join `m_office` `s` ON ((`boc`.`office_id` = `s`.`id`)))
        join `b_charge` `bc` ON ((`boc`.`charge_id` = `bc`.`id`)))
        join `m_client` `mc` ON ((`bc`.`client_id` = `mc`.`id`)))
        join `b_client_address` `bca` ON (((`bc`.`client_id` = `bca`.`client_id`)
            and (`bca`.`address_key` = 'PRIMARY'))))
        left join `b_onetime_sale` `bos` ON (((`bc`.`order_id` = `bos`.`id`)
            and (`bc`.`priceline_id` = 0))))
        left join `b_item_master` `im` ON ((`bos`.`item_id` = `im`.`id`)))
        left join `b_orders` `bo` ON (((`bc`.`order_id` = `bo`.`id`)
            and (`bc`.`priceline_id` <> 0))))
        left join `b_plan_master` `bpm` ON ((`bo`.`plan_id` = `bpm`.`id`)));

UPDATE stretchy_report SET report_sql='select * from v_agent_commission where `INVOICE DATE` between ''${startDate}'' and ''${endDate}''', 
core_report=0 WHERE  report_name='Reseller Commission Details';

UPDATE stretchy_report SET report_sql='SELECT   `RESELLER ID`,`RESELLER NAME`,`COUNTRY`,  `PLAN NAME` AS `PLAN/ITEAM`, `MONTH`, 
`SOURCE`,`SHARE TYPE`,count(`CLIENT ID`) `NO OF UNITS`,  
cast(round(sum(`CHARGE AMOUNT`),2) as char) `CHARGE AMOUNT`,`SHARE AMOUNT`,
cast(round(sum(`COMM AMOUNT`),2) as char) `COMMISSION`
FROM v_agent_commission
WHERE `INVOICE DATE`  BETWEEN ''${startDate}'' and ''${endDate}''
GROUP BY `PLAN/ITEAM`,SOURCE,MONTH,`RESELLER ID`,`SHARE TYPE`', 
core_report=0 WHERE  report_name='Reseller Commission Details';

UPDATE stretchy_report SET core_report=0 WHERE report_category='Table';

