CREATE TABLE if not exists `m_office_statistics` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `client_active` bigint(20) DEFAULT 0,
  `client_inactive` bigint(20) DEFAULT 0,
  `in_stock` bigint(20) DEFAULT 0,
  `stock_allocated` bigint(20) DEFAULT 0, 
  PRIMARY KEY (`id`),
  KEY `stats_office_key` (`office_id`),
  CONSTRAINT `stats_office_key` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert ignore into m_office_statistics  select id,id,0,0,0,0 from m_office;

Drop procedure IF EXISTS update_stats;
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_stats`()
BEGIN
DECLARE v_finished INTEGER DEFAULT 0;
DECLARE v_officeid INTEGER DEFAULT 0;
DECLARE l_text varchar(20) default NULL;
DECLARE v_stock INTEGER DEFAULT 0;
DECLARE v_allocated INTEGER DEFAULT 0;

DEClARE i_cursor CURSOR FOR
Select office_id,case when counts=1 then 'Allocated' else 'In Stock' end as stock_status ,
count(0) from (select office_id, case when client_id>0 then 1 else 0 end as counts
from b_item_detail where item_master_id=1) x group by counts;

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
Drop procedure IF EXISTS update_stats;


Drop procedure IF EXISTS update_stat;
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_stat`()
BEGIN
DECLARE v_finished INTEGER DEFAULT 0;
DECLARE v_officeid INTEGER DEFAULT 0;
DECLARE l_text varchar(20) default NULL;
DECLARE v_stock INTEGER DEFAULT 0;
DECLARE v_allocated INTEGER DEFAULT 0;

DEClARE i_cursor CURSOR FOR
select c.id,a.status,count(0) counts from b_client_service a, m_client b,m_office c where a.client_id=b.id and b.office_id=c.id group by a.status,c.id;

DECLARE CONTINUE HANDLER
        FOR NOT FOUND SET v_finished = 1;
OPEN i_cursor;
get_stats: LOOP

FETCH i_cursor INTO v_officeid,l_text,v_stock;
IF v_finished = 1 THEN
LEAVE get_stats;
END IF;
if l_text ='ACTIVE' then
Update m_office_statistics set  client_active=v_stock where office_id = v_officeid;
else
Update m_office_statistics set  client_inactive=v_stock where office_id = v_officeid;
end if;
END LOOP get_stats;
CLOSE i_cursor;

END //
DELIMITER ;
Drop procedure IF EXISTS update_stat;


SET GLOBAL event_scheduler = ON;


CREATE EVENT IF NOT EXISTS `dashboard`
  ON SCHEDULE 
EVERY 1 HOUR
   COMMENT ' Run this for every 1 hour to update the dashboard'
  DO  call update_stats();

  CREATE EVENT IF NOT EXISTS dashboard
    ON SCHEDULE
      EVERY 1 HOUR
    COMMENT 'Update dashboard statistics each hour.'
    DO call update_stats();


