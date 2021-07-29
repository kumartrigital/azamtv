Drop procedure IF EXISTS addcontractToPrice; 
DELIMITER //
create procedure addcontractToPrice() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'duration'
     and TABLE_NAME = 'b_plan_pricing'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_plan_pricing add column duration varchar(20) DEFAULT NULL;

END IF;
END //
DELIMITER ;
call addcontractToPrice();
Drop procedure IF EXISTS addcontractToPrice; 

Drop procedure IF EXISTS updatecontractToPrice; 
DELIMITER //
create procedure updatecontractToPrice() 
Begin
  IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'duration'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
UPDATE b_plan_pricing t1, b_plan_master t2 SET t1.duration = t2.duration WHERE t1.plan_id = t2.id;

END IF;
END //
DELIMITER ;
call updatecontractToPrice();
Drop procedure IF EXISTS updatecontractToPrice;


Drop procedure IF EXISTS dropcontractInPlan;
DELIMITER //
create procedure dropcontractInPlan() 
Begin
  IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'duration'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_plan_master drop column duration;

END IF;
END //
DELIMITER ;
call dropcontractInPlan();
Drop procedure IF EXISTS dropcontractInPlan;

Drop procedure IF EXISTS addPlanImage;
DELIMITER //
create procedure addPlanImage() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'image'
     and TABLE_NAME = 'b_prov_plan_details'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_prov_plan_details add column image varchar(20) DEFAULT NULL;

END IF;
END //
DELIMITER ;
call addPlanImage();
Drop procedure IF EXISTS addPlanImage;

insert ignore into `m_permission`(`id`,`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`) values (null,'billing','UPDATE_IPDETAILPARAMS','IPDETAILPARAMS','UPDATE',0);

