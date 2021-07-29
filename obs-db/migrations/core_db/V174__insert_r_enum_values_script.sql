insert ignore into r_enum_value VALUES ('radius',1,'version-1','version-1');
insert ignore into r_enum_value VALUES ('radius',2,'version-2','version-2');
insert ignore into r_enum_value VALUES ('device_swap',1,'Replacement','Replacement');

Drop procedure IF EXISTS addWarrantyDate;
DELIMITER //
create procedure addWarrantyDate() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'warranty_date'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_item_detail` ADD COLUMN `warranty_date` DATETIME NULL DEFAULT NULL  AFTER `warranty` ;

END IF;
END //
DELIMITER ;
call addWarrantyDate();
Drop procedure IF EXISTS addWarrantyDate;

SET SQL_SAFE_UPDATES = 0;
update job_parameters set param_name='system' where param_name='ProvSystem';

SET @id=(select id from job where name='RADIUS');
insert ignore into job_parameters values(null,@id,'Mikrotik_api','String',null,'{"ip":"","uname":"","pwd":""}','N',null);


