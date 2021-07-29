Drop procedure IF EXISTS addcolumn;
DELIMITER //
create procedure addcolumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_pairing'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_item_detail add column is_pairing char(1) NOT NULL after `location_id`;

END IF;
END //
DELIMITER ;
call addcolumn();
Drop procedure IF EXISTS addcolumn;


Drop procedure IF EXISTS addcolumn;
DELIMITER //
create procedure addcolumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'paired_item_id'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_item_detail add column paired_item_id int(20) default NULL after `is_pairing`;
END IF;
END //
DELIMITER ;
call addcolumn();
Drop procedure IF EXISTS addcolumn;

INSERT IGNORE INTO m_code VALUES (null,'SP',0,'Service Params');
SET @id = (select id from m_code where code_name='SP');

INSERT IGNORE INTO m_code_value VALUES (null,@id,'Network_node',1);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Technology',2);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'NSTV',3);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Latence',4);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'ABV',5);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'LogicEstern',6);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'CTI',7);
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Only1CAS',8);

SET @id1 = (select id from m_code where code_name='Provisioning');

DELETE FROM `m_code_value` WHERE `code_id` =
(select id from m_code where code_name = 'Provisioning');

INSERT IGNORE INTO m_code_value VALUES (null,@id1,'None',0);
INSERT IGNORE INTO m_code_value VALUES (null,@id1,'NSTV',1);
INSERT IGNORE INTO m_code_value VALUES (null,@id1,'Latence',2);
INSERT IGNORE INTO m_code_value VALUES (null,@id1,'ABV',3);
INSERT IGNORE INTO m_code_value VALUES (null,@id1,'LogicEstern',4);
INSERT IGNORE INTO m_code_value VALUES (null,@id1,'CTI',5);
INSERT IGNORE INTO m_code_value VALUES (null,@id1,'Only1CAS',6);

insert IGNORE into m_permission (grouping,code,entity_name,action_name) values ('portfolio','READ_CLIENTSERVICE','CLIENTSERVICE','READ');
