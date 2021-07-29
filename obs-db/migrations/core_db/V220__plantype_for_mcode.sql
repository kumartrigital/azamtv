Drop procedure IF EXISTS addPlanTypeColumn;
DELIMITER //
create procedure addPlanTypeColumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'plan_type'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_plan_master add column plan_type BIGINT(10) NOT NULL AFTER `allow_topup`;

END IF;
END //
DELIMITER ;
call addPlanTypeColumn();
Drop procedure IF EXISTS addPlanTypeColumn;

Drop procedure IF EXISTS addPlanTypeNameColumn;
DELIMITER //
create procedure addPlanTypeNameColumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'plan_type_name'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_plan_master add column plan_type_name VARCHAR(45) NOT NULL AFTER `plan_type`;

END IF;
END //
DELIMITER ;
call addPlanTypeNameColumn();
Drop procedure IF EXISTS addPlanTypeNameColumn;



insert ignore into m_code(code_name, is_system_defined, code_description) 
values ('plan Type',0,'defination of plan type');

set @id =(select id from m_code where code_name='plan Type');


insert ignore into m_code_value(code_id,code_value,order_position) 
values(@id,'General',0);

insert ignore into m_code_value(code_id,code_value,order_position)
 values(@id,'Hardware',1);

