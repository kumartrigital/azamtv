Drop procedure IF EXISTS jobParameters; 
DELIMITER //
create procedure jobParameters() 
Begin
IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='param_value'  
      and TABLE_NAME = 'job_parameters'
     and TABLE_SCHEMA = DATABASE())THEN
alter table job_parameters change column param_value param_value varchar(500) NULL DEFAULT NULL;
END IF;
END //
DELIMITER ;
call jobParameters();
Drop procedure IF EXISTS jobParameters;
SET SQL_SAFE_UPDATES = 0;
update job_parameters set param_value='{"ip":"","userName":"","password":"","port":"8728","type":"pppoe"}' where param_name 
like 'Mikrotik_api%';

