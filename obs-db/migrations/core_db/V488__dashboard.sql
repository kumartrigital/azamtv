SET sql_safe_updates=0;

Update m_appuser set email='admin@clientmail.com' where id in (0,1,2);
--alter table m_office_balance add credit_limit decimal(24,4);
alter table m_office_balance add wallet_amount decimal(24,4);


DROP TABLE IF EXISTS `m_office_statistics`;

CREATE TABLE IF NOT EXISTS `m_office_statistics` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `client_active` bigint(20) DEFAULT 0,
  `client_inactive` bigint(20) DEFAULT 0,
  `in_stock` bigint(20) DEFAULT 0,
  `stock_allocated` bigint(20) DEFAULT 0,  
  `voucher_stock` bigint(20) DEFAULT 0,
  `provision_pending` bigint(20) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `stats_office_key` (`office_id`),
  CONSTRAINT `stats_office_key` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert  into m_office_statistics  select id,id,0,0,0,0,0,0 from m_office;

drop  procedure IF EXISTS update_stat3;


DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_stat3`()
BEGIN
DECLARE v_finished INTEGER DEFAULT 0;
DECLARE v_officeid INTEGER DEFAULT 0;
DECLARE voucher_stock INTEGER DEFAULT 0;

 DEClARE i_cursor CURSOR FOR
	select office_id,count(1) from b_pin_details where status!='used' group by 1;

 DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_finished = 1;
 OPEN i_cursor;
 get_stats: LOOP

 FETCH i_cursor INTO v_officeid,voucher_stock;
 IF v_finished = 1 THEN
	LEAVE get_stats;
 END IF;
	update m_office_statistics set voucher_stock=voucher_stock where office_id = v_officeid;
 END LOOP get_stats;
 CLOSE i_cursor;
 
END //
DELIMITER ;

drop  procedure IF EXISTS update_stat4;

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_stat4`()
BEGIN
DECLARE v_finished INTEGER DEFAULT 0;
DECLARE v_officeid INTEGER DEFAULT 0;
DECLARE provision_pending INTEGER DEFAULT 0;

 DEClARE i_cursor CURSOR FOR
	select c.office_id,count(1) from m_client c 
 JOIN
`b_orders` `o` ON `c`.`id` = `o`.`client_id`
 JOIN
`b_provisioning_request_detail` `bpr` ON COALESCE(JSON_EXTRACT(request_message,
'$.newOrderList[0].orderId'),
JSON_EXTRACT(request_message,
'$.oldOrderList[0].orderId')) = o.id
 JOIN
`b_provisioning_request` `bp` ON `bp`.`id` = `bpr`.`provisioning_req_id`
WHERE
`o`.order_status = 4 
AND bp.status IN ('F' , 'N') and date(bp.start_date)=date(curdate())
GROUP BY 1; 

 DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_finished = 1;
 OPEN i_cursor;
 get_stats: LOOP

 FETCH i_cursor INTO v_officeid,provision_pending;
 IF v_finished = 1 THEN
	LEAVE get_stats;
 END IF;
	update m_office_statistics set provision_pending=provision_pending where office_id = v_officeid;
 END LOOP get_stats;
 CLOSE i_cursor;
 
END //
DELIMITER ;


drop  procedure IF EXISTS update_stat2;
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_stat2`()
BEGIN
DECLARE v_finished INTEGER DEFAULT 0;
DECLARE v_officeid INTEGER DEFAULT 0;
DECLARE l_text varchar(20) default NULL;
DECLARE v_stock INTEGER DEFAULT 0;
DECLARE v_allocated INTEGER DEFAULT 0;

 DEClARE i_cursor CURSOR FOR
 Select office_id,case when counts=1 then 'Allocated' else 'In Stock' end as stock_status ,
	count(0) from (select office_id, case when client_id>0 then 1 else 0 end as counts 
	from b_item_detail where item_master_id in  (select id from b_item_master where item_class=1 and is_deleted = 'n')) x group by 1,2;

 DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_finished = 1;
 OPEN i_cursor;
 get_stats: LOOP

 FETCH i_cursor INTO v_officeid,l_text,v_stock;
 IF v_finished = 1 THEN
	LEAVE get_stats;
 END IF;
	if l_text ='Allocated' then
	 Update m_office_statistics set  stock_allocated=v_stock where office_id = v_officeid;
	else
	 Update m_office_statistics set  in_stock=v_stock where office_id = v_officeid;
	end if;
 END LOOP get_stats;
 CLOSE i_cursor;
 
END //
DELIMITER ;


drop procedure IF EXISTS update_stat1;

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_stat1`()
BEGIN
DECLARE v_finished INTEGER DEFAULT 0;
DECLARE v_officeid INTEGER DEFAULT 0;
DECLARE l_text varchar(20) default NULL;
DECLARE v_stock INTEGER DEFAULT 0;
DECLARE v_allocated INTEGER DEFAULT 0;

 DEClARE i_cursor CURSOR FOR
 select c.id,b.status_enum,count(0) counts from b_client_service a, m_client b,m_office c ,b_allocation d
 where a.client_id=b.id and b.office_id=c.id and a.client_id=d.client_id and a.status in ('ACTIVE','SUSPENDED') and d.status='allocated' 
 and a.service_id>0 and b.status_enum in (300,600) group by 1,2;
 DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_finished = 1;
 OPEN i_cursor;
 get_stats: LOOP

 FETCH i_cursor INTO v_officeid,l_text,v_stock;
 IF v_finished = 1 THEN
	LEAVE get_stats;
 END IF;
	if l_text ='300' then
	 Update m_office_statistics set  client_active=v_stock where office_id = v_officeid;
	else
	 Update m_office_statistics set  client_inactive=v_stock where office_id = v_officeid;
	end if;
 END LOOP get_stats;
 CLOSE i_cursor;
 
END //
DELIMITER ;

drop procedure IF EXISTS update_stats;


DELIMITER //
CREATE PROCEDURE update_stats()
BEGIN
insert into m_office_statistics  select id,id,0,0,0,0,0,0 from m_office where id not in (select office_id from m_office_statistics);
 call update_stat1();
 call update_stat2();
 call update_stat3();
 call update_stat4();
END //
DELIMITER ;



DROP EVENT  IF EXISTS dashboard;

SET GLOBAL event_scheduler = ON;


	

CREATE DEFINER=`root`@`localhost` EVENT `dashboard` ON SCHEDULE EVERY
 20 MINUTE DO call update_stats();





	
Select count(0) lco_cnt,sum(active) as c_active,sum(inactive) as c_inactive , sum(instock) as c_instock,sum(allocated) as c_allocated from (
select a.id,IFNULL(b.client_active, 0) as active,IFNULL(b.client_inactive, 0) as inactive
,IFNULL(b.in_stock, 0) as instock,IFNULL(b.stock_allocated, 0) as allocated
from m_office a left join m_office_statistics b on a.id=b.office_id where a.hierarchy like '%') X;


drop procedure IF EXISTS update_dash;

---> Single procedure 


DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_dash`()
BEGIN
	DECLARE v_finished INTEGER DEFAULT 0;
begin
DECLARE v_officeid INTEGER DEFAULT 0;
DECLARE l_text varchar(20) default NULL;
DECLARE v_stock INTEGER DEFAULT 0;
DECLARE v_allocated INTEGER DEFAULT 0;

 DEClARE i_cursor CURSOR FOR
  Select office_id,case when counts=1 then 'Allocated' else 'In Stock' end as stock_status ,
	count(0) from (select office_id, case when client_id>0 then 1 else 0 end as counts 
	from b_item_detail where item_master_id=1) x group by 1,2;


 DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_finished = 1;
 OPEN i_cursor;
 get_stats: LOOP

 FETCH i_cursor INTO v_officeid,l_text,v_stock;
 IF v_finished = 1 THEN
	LEAVE get_stats;
 END IF;
	if l_text ='Allocated' then
	 Update m_office_statistics set  stock_allocated=v_stock where office_id = v_officeid;
	else
	 Update m_office_statistics set  in_stock=v_stock where office_id = v_officeid;
	end if;
 END LOOP get_stats;
 CLOSE i_cursor;
End;

Begin
DECLARE v_officeid INTEGER DEFAULT 0;
DECLARE l_text varchar(20) default NULL;
DECLARE v_stock INTEGER DEFAULT 0;
DECLARE v_allocated INTEGER DEFAULT 0;

 DEClARE i_cursor CURSOR FOR
 select c.id,b.status_enum,count(0) counts from b_client_service a, m_client b,m_office c ,b_allocation d
 where a.client_id=b.id and b.office_id=c.id and a.client_id=d.client_id and a.status='ACTIVE' and d.status='allocated' 
 and a.service_id>0 and b.status_enum in (300,600) group by 1,2;

 DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_finished = 1;
 OPEN i_cursor;
 get_stats: LOOP

 FETCH i_cursor INTO v_officeid,l_text,v_stock;
 IF v_finished = 1 THEN
	LEAVE get_stats;
 END IF;
	if l_text ='300' then
	 Update m_office_statistics set  client_active=v_stock where office_id = v_officeid;
	else
	 Update m_office_statistics set  client_inactive=v_stock where office_id = v_officeid;
	end if;
 END LOOP get_stats;
 CLOSE i_cursor;
End;
 
END //
DELIMITER ;


