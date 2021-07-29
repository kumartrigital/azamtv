Drop procedure IF EXISTS addpriceinaddon;
DELIMITER //
create procedure addpriceinaddon() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'price_id'
     and TABLE_NAME = 'b_orders_addons'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_orders_addons add column `price_id` bigint(10) default NULL;

END IF;
END //
DELIMITER ;
call addpriceinaddon();
Drop procedure IF EXISTS addpriceinaddon;

Drop procedure IF EXISTS addaddoninprice;
DELIMITER //
create procedure addaddoninprice() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_addon'
     and TABLE_NAME = 'b_order_price'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_order_price add column `is_addon` char(1) default 'N' after `tax_inclusive`;

END IF;
END //
DELIMITER ;
call addaddoninprice();
Drop procedure IF EXISTS addaddoninprice;
update b_order_price set is_addon = 'N';
alter TABLE b_orders_addons modify end_date datetime default null;
insert ignore into m_permission values(null,'client&orders','DISCONNECT_ORDERADDONS','ORDERADDONS','DISCONNECT',0);
alter TABLE b_orders_addons modify status varchar(50) NOT NULL;


set @id = (select id from job where name ='AUTO_EXIPIRY' OR display_name='AUTO EXIPIRY');

insert ignore into `job_parameters`(`id`,`job_id`,`param_name`,`param_type`,`param_default_value`,`param_value`,`is_dynamic`,`query_values`) values (null,@id,'addonExipiry','COMBO','N','N','N',null);

Drop procedure IF EXISTS addTitleInMClient;
DELIMITER //
create procedure addTitleInMClient() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'title'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table m_client add column `title` VARCHAR(15) DEFAULT NULL;

END IF;
END //
DELIMITER ;
call addTitleInMClient();
Drop procedure IF EXISTS addTitleInMClient;


