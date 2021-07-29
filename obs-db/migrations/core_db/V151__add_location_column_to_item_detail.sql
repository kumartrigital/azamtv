Drop procedure IF EXISTS addlocation;
DELIMITER //
create procedure addlocation() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'location_id'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_item_detail add column location_id bigint(10) NOT NULL;

END IF;
END //
DELIMITER ;
call addlocation();
Drop procedure IF EXISTS addlocation;


SET SQL_SAFE_UPDATES = 0;

delete from m_code_value where code_id=(select id from m_code where m_code.code_name='Item Quality');
delete from m_code where code_name='Item Quality';
insert ignore into m_code (id,code_name,is_system_defined,code_description) VALUES (null,'Item Quality',0,'define haraware condition');
select @a_lid:=(select id from m_code where code_name='Item Quality');
insert ignore into m_code_value VALUES (null,@a_lid,'Good',0);
insert ignore into m_code_value VALUES (null,@a_lid,'Refurbished',0);
insert ignore into m_code_value VALUES (null,@a_lid,'Faulty',0);

delete from m_code_value where code_id=(select id from m_code where m_code.code_name='Item Status');
delete from m_code where code_name='Item Status';
insert ignore into m_code (id,code_name,is_system_defined,code_description) VALUES (null,'Item Status',0,'define haraware staus');
select @a_lid:=(select id from m_code where code_name='Item Status');
insert ignore into m_code_value VALUES (null,@a_lid,'Available',0);
insert ignore into m_code_value VALUES (null,@a_lid,'UnAvailable',0);
insert ignore into m_code_value VALUES (null,@a_lid,'In Use',0);

 update b_item_detail set status ='Available' where status='NEW' and quality ='Good';
 update b_item_detail set status ='UnAvailable' where status='NEW' and quality !='Good';
 update b_item_detail set status ='In Use' where status='Used';
SET SQL_SAFE_UPDATES = 1;