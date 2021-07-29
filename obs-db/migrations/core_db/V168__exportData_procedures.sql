drop procedure if exists p_int_fa0 ;
DELIMITER //

create procedure p_int_fa0( p_todt date)

begin

create table IF NOT EXISTS INT_FA 
(id int(10) not null AUTO_INCREMENT,
int_date datetime,
obsTable varchar(50),
From_id  bigint(20),
to_id    bigint(20),
from_dt  dateTIME,
to_dt	 dateTIME,
records	 int(10),
filename	varchar(50),
createdby_id int(10),
created_dt datetime,
PRIMARY KEY (`id`)
);

insert into INT_FA
select null id,current_date() int_date, 'm_client' obsTable,
min(id) fromId,max(id) toId, 
min(activation_date) fromdt, max(activation_date) todt,count(id) records, 
'clients.csv',1 createdby_id,current_date() created_dt
from m_client 
where activation_date <= p_todt  
union all
select null id, current_date() int_date,'b_invoice' obsTable,
min(id),max(id), min(invoice_date) , max(invoice_date) , count(id), 'invoices.csv',
1 createdby_id,now() created_dt
from b_invoice 
where invoice_date <= p_todt  
union all 
select null id, current_date() int_date,'b_payments' obsTable,
min(id),max(id), min(payment_date) , max(payment_date) , count(id), 'payments.csv',
1 createdby_id,now() created_dt
from b_payments 
where payment_date <= p_todt 
union all 
select null id, current_date() int_date,'b_adjustments' obsTable,
min(id),max(id), min(adjustment_date) , max(adjustment_date) , count(id), 'adjustments.csv',
1 createdby_id,now() created_dt
from b_adjustments 
where adjustment_date <= p_todt
;
end //

DELIMITER ;

SET @date1=current_date();
call p_int_fa0(@date1);


Drop procedure if exists loginHistory;
DELIMITER //

create procedure loginHistory()
BEGIN
IF EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='username' 
AND TABLE_NAME='b_login_history'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE b_login_history MODIFY username varchar(100) NOT NULL;
END IF;
END // 
DELIMITER ;
call loginHistory();
Drop procedure if exists loginHistory;


drop procedure if exists p_int_fa ;
DELIMITER //


CREATE PROCEDURE `p_int_fa`(p_todt date)

begin

DECLARE _exists  TINYINT(1) DEFAULT 0;

    SELECT COUNT(*) INTO _exists
    FROM information_schema.tables 
    WHERE table_schema =  DATABASE()
    AND table_name =  'INT_FA';



if _exists =0 then call p_int_fa0(p_todt) ;
else 
insert into INT_FA 
select * from 
(select null id,current_date() int_date, 'm_client' obsTable,
min(id) fromId,max(id) toId, 
min(activation_date) fromdt, max(activation_date) todt,count(id) records, 
'clients.csv',1 createdby_id,current_date() created_dt
from m_client 
where activation_date between 

(select max(to_dt) from INT_FA where obsTable='m_client')  and p_todt 
and id > (select max(to_id) from INT_FA where obsTable='m_client')

union all
select null id, current_date() int_date,'b_invoice' obsTable,
min(id),max(id), min(invoice_date) , max(invoice_date) , count(id), 'invoices.csv',
1 createdby_id,now() created_dt
from b_invoice 
where invoice_date between 

(select max(to_dt) from INT_FA where obsTable='b_invoice')  and p_todt 
and id > (select max(to_id) from INT_FA where obsTable='b_invoice')

union all 
select null id, current_date() int_date,'b_payments' obsTable,
min(id),max(id), min(payment_date) , max(payment_date) , count(id), 'payments.csv',
1 createdby_id,now() created_dt
from b_payments 
where payment_date between 

(select max(to_dt) from INT_FA where obsTable='b_payments')  and p_todt
and id > (select max(to_id) from INT_FA where obsTable='b_payments')

union all 
select null id, current_date() int_date,'b_adjustments' obsTable,
min(id),max(id), min(adjustment_date) , max(adjustment_date) , count(id), 'adjustments.csv',
1 createdby_id,now() created_dt
from b_adjustments 
where adjustment_date between 

(select max(to_dt) from INT_FA where obsTable='b_adjustments')  and p_todt
 and id > (select max(to_id) from INT_FA where obsTable='b_adjustments')

)a
where records >0;

end if;

end //
DELIMITER ;



CREATE OR REPLACE VIEW `int_fa_v` AS 
select cast(`i`.`int_date` as date) AS `int_date`,
'm_clients' AS `obsTable`,
`mc`.`id` AS `client_id`,
`mc`.`account_no` AS `id`,
cast(`mc`.`activation_date`as date) AS `date`,
0 AS `amount`,
`mc`.`display_name` AS `details` ,
'New' as `transactionType`
from (`INT_FA` `i` join `m_client` `mc`) where (`mc`.`id` between `i`.`From_id` and `i`.`to_id`) 
union all 
select cast(`i`.`int_date` as date) AS `int_date`,
'b_invoice' AS `obsTable`,
`b`.`client_id` AS `client_id`,
`b`.`id` AS `id`,
cast(`b`.`invoice_date` as date) AS `invoice_date`,
-- cast(round(`b`.`invoice_amount`,2) as char charset latin1) AS `invoice_amount`,
(case when(`b`.`invoice_amount` > 0) 
     then concat('+',(`b`.`invoice_amount`)) 
    when(`b`.`invoice_amount` < 0) 
     then concat('-',abs(`b`.`invoice_amount`)) else 0 end) AS `invoice_amount`,
`b`.`invoice_status` AS `invoice_status` ,
'Invoice' as `transactionType`
from (`INT_FA` `i` join `b_invoice` `b`) where (`b`.`id` between `i`.`From_id` and `i`.`to_id`) 
union all 
select cast(`i`.`int_date` as date) AS `int_date`,
'b_payments' AS `obsTable`,
`b`.`client_id` AS `client_id`,
`b`.`id` AS `id`,
cast(`b`.`payment_date` as date) AS `payment_date`,
concat('-',round(`b`.`amount_paid`,2)) AS `amount_paid`,
`b`.`Remarks` AS `remarks`, 
'Payment' as `transactionType`
from (`INT_FA` `i` join `b_payments` `b`) where (`b`.`id` between `i`.`From_id` and `i`.`to_id`) 
union all
 select cast(`i`.`int_date` as date) AS `int_date`,
'b_adjustments' AS `obsTable`,
`b`.`client_id` AS `client_id`,
`b`.`id` AS `id`,
cast(`b`.`adjustment_date` as date) AS `adjustment_date`,
(case when (`b`.`adjustment_type`='DEBIT')
 then concat('+',`b`.`adjustment_amount`)
 when (`b`.`adjustment_type` ='CREDIT')
 then concat('-',`b`.`adjustment_amount`) else 0 end)AS `adjustment_amount`,
`b`.`adjustment_type` AS `adjustment_type`,
'Adjustment' as `transactionType`
 from (`INT_FA` `i` join `b_adjustments` `b`) where (`b`.`id` between `i`.`From_id` and `i`.`to_id`);

/*--
INSERT IGNORE INTO `stretchy_report` VALUES(null,'List of Customers', 'Table', '', 'Client', 'SELECT id as Id ,transactionType,details as Name FROM int_fa_v where obsTable=''m_clients'' and int_date  between ''${startDate}'' and ''${endDate}''', 'export newly created customers to other financial  systems', '0', '1');
SET @ID=(SELECT id FROM stretchy_report WHERE report_name='List of Customers');
INSERT IGNORE INTO `stretchy_report_parameter` VALUES (null, @ID, 1, 'From Date');
INSERT IGNORE INTO `stretchy_report_parameter` VALUES (null, @ID, 2, 'To Date');

INSERT IGNORE INTO `stretchy_report` VALUES(null,'List of Transactions','Table', '', 'Billing', 'SELECT id ,client_id as clientId,transactionType,amount FROM int_fa_v where int_date between ''${startDate}'' and ''${endDate}'' and obstable<>''m_clients'' order by clientId', 'exports client transactions to other financial systems', '0', '1');
SET @ID=(SELECT id FROM stretchy_report WHERE report_name='List of Transactions');
INSERT IGNORE INTO `stretchy_report_parameter` VALUES (null, @ID, 1, 'From Date');
INSERT IGNORE INTO `stretchy_report_parameter` VALUES (null, @ID, 2, 'To Date');
--!> */




