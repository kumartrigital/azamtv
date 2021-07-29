Drop procedure IF EXISTS addParamLengthColumn;
DELIMITER //
create procedure addParamLengthColumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'param_length'
     and TABLE_NAME = 'b_command_parameters'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_command_parameters ADD COLUMN `param_length` INT(4) NULL AFTER `param_default`;

END IF;
END //
DELIMITER ;
call addParamLengthColumn();
Drop procedure IF EXISTS addParamLengthColumn;


ALTER TABLE `b_provisioning_request` 
CHANGE COLUMN `order_id` `order_id` BIGINT(20) NULL DEFAULT NULL ;


set @proid =(select id from m_code where code_name='Provisioning');
set @id =(select id from m_code_value where code_value='ABV'  and code_id = @proid);

insert ignore into b_command(provisioning_system, command_name, status) 
values (@id,'Fingerprint','A');

insert ignore into b_command(provisioning_system, command_name, status) 
values (@id,'OSD','A');

insert ignore into b_command(provisioning_system, command_name, status) 
values (@id,'BMail','A');


set @id1 =(select id from b_command where command_name='Fingerprint');
set @id2 =(select id from b_command where command_name='OSD');
set @id3 =(select id from b_command where command_name='BMail');

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id1,'XAxis','Text',null,2);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id1,'YAxis','Text',null,2);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id1,'Text Size','Text',null,2);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id1,'BG Color','Text',null,7);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id1,'Text color','Text',null,7);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id1,'Duration','Text',60,2);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id1,'Repetition','Text',null,1);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id1,'Interval','Text',null,2);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id2,'Message Subject','Text',null,20);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id2,'Message','Text',null,20);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id2,'Duration','Text',60,2);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id2,'Repetition','Text',null,1);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id2,'Interval','Text',null,2);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id3,'Message Subject','Text',null,20);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id3,'Message','Text',null,20);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id3,'Duration','Text',60,2);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id3,'Repetition','Text',null,1);

insert ignore into b_command_parameters(command_id, command_param, param_type, param_default, param_length) 
values (@id3,'Interval','Text',null,2);

