DROP FUNCTION IF EXISTS `fbal`;
DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `fbal`(cid int, dt date) RETURNS int(11)
BEGIN

declare v_bal double ;
select ( ifnull(sum(`bicl`.`invoice_amount`),0) -
    ifnull(sum(distinct `bpcl`.`amount_paid`),0))  into v_bal 
	from (`b_invoice` `bicl` left join `b_payments` `bpcl` on(
	(`bicl`.`client_id` = `bpcl`.`client_id`) and  	 ( bpcl.is_deleted=0) and
									`bpcl`.`payment_date` <= dt  ))
	where ((`bicl`.`invoice_date` <=  dt ) 
     and (`bicl`.`client_id` =  cid  )) 
;

return v_bal;
END$$
DELIMITER ;

DROP FUNCTION IF EXISTS `fopbal`;
DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `fopbal`(cid int, dt date) RETURNS int(11)
BEGIN

declare v_bal double ;
select ( ifnull(sum(`bicl`.`invoice_amount`),0) -
    ifnull(sum(distinct `bpcl`.`amount_paid`),0))  into v_bal 
	from (`b_invoice` `bicl` left join `b_payments` `bpcl` on(
	(`bicl`.`client_id` = `bpcl`.`client_id`) and  	 ( bpcl.is_deleted=0) and
									`bpcl`.`payment_date` < dt  ))
	where ((`bicl`.`invoice_date` <  dt ) 
     and (`bicl`.`client_id` =  cid  )) 
;
return v_bal;
END$$
DELIMITER ;


CREATE OR REPLACE  VIEW `netrevenueDtls_vw` 
AS 
select `c`.`id` AS `client_id`,`dt`.`year4` AS `year4`,
`dt`.`month_number` AS `mon`,
`dt`.`year_month_abbreviation` AS `year_mon`,
dt.date_value date,
fopbal(c.id,dt.date_value) opbal,
bi.id inv_id,
bi.invoice_date ,
sum(round(`bi`.`invoice_amount`,2)) AS `inv_amt`,
bp.id pmt_id,
bp.payment_date ,
sum(round(ifnull(`bp`.`amount_paid`,0),2)) AS `pmt_amt`,
`ldm`.`date_value` AS `month_end`,
fbal(c.id,dt.`date_value` ) fclbal,
cb.balance_amount current_balance
from  ((((`m_client` `c` 
join `dim_date` `dt` on(((`dt`.`year4` = 2015) and (`dt`.`month_number` <= month(now()))))) 
join `dim_date` `ldm` on(((`dt`.`year_month_number` = `ldm`.`year_month_number`) and (`ldm`.`is_last_day_in_month` = 'Yes')))) 
join b_client_balance cb on (c.id = cb.client_id )
left join `b_invoice` `bi` on (( `c`.`id` = `bi`.`client_id` ) and (bi.invoice_date = dt.date_value )))
left join `b_payments` `bp` on(((`c`.`id` = `bp`.`client_id`) and (bp.payment_date = dt.date_value ) and (bp.is_deleted=0)
and (`dt`.`year_month_number` = concat(year(`bp`.`payment_date`),'-',month(`bp`.`payment_date`)))))) 
where (bi.id is not null or bp.id is not null)
group by `bi`.`client_id`,`dt`.`date_value` ,`bi`.`id`,`bp`.`id`
order by `c`.`id`,`dt`.`year4`,`dt`.`date_value`;


INSERT IGNORE INTO stretchy_report VALUES(NULL, 'UnpaidCustomers', 'Table', '', 'Scheduling Job', 
'Select client_id as clientId,year_mon,date,opbal,inv_id,invoice_date last_inv_dt,
DATE_ADD(invoice_date,INTERVAL 7 DAY) due_date,
inv_amt,pmt_id,payment_date last_pmt_dt,pmt_amt,month_end,fclbal,current_balance 
from netrevenueDtls_vw  nrv
where nrv.inv_id = 
(select MAX(id) from b_invoice bi where bi.client_id = nrv.client_id  )
and current_balance >0 and DATE_ADD(invoice_date,INTERVAL 7 DAY) <=NOW()
order by  client_id,year_mon,date', 'Disconnect Unpaid Customers', '0', '0');


INSERT IGNORE INTO job VALUES(NULL, 'DISCONNECT_UNPAID_CUSTOMERS', 'Disconnect Unpaid Customers', '0 0 0 1 1/1 ? *', 'First Day at Midnight of Every Month',
 now(), '5', NULL, now(), NULL, 'DISCONNECT_UNPAID_CUSTOMERSJobDetaildefault _ DEFAULT', NULL, '0', '0', '1', '0', '0', '1');

SET @ID=(select id from job where name='DISCONNECT_UNPAID_CUSTOMERS');
INSERT IGNORE INTO job_parameters VALUES(NULL, @ID, 'reportName', 'COMBO', NULL, 'UnpaidCustomers', 'N', NULL);




