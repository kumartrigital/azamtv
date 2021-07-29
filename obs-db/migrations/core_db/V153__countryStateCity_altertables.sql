Drop procedure IF EXISTS alterTableCountry; 
DELIMITER //
create procedure alterTableCountry() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'country_code' and DATA_TYPE='varchar'
     and TABLE_NAME = 'b_country'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table b_country change country_code country_code varchar(15)  NOT NULL;
END IF;
END //
DELIMITER ;
call alterTableCountry();
Drop procedure IF EXISTS alterTableCountry; 

Drop procedure IF EXISTS alterTableState; 
DELIMITER //
create procedure alterTableState() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'state_code' and DATA_TYPE='varchar'
     and TABLE_NAME = 'b_state'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table b_state change state_code state_code varchar(20)  NOT NULL;
END IF;
END //
DELIMITER ;
call alterTableState();
Drop procedure IF EXISTS alterTableState; 

Drop procedure IF EXISTS alterTableCity; 
DELIMITER //
create procedure alterTableCity() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'city_code' and DATA_TYPE='varchar'
     and TABLE_NAME = 'b_city'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table b_city change city_code city_code varchar(20)  NOT NULL;
END IF;
END //
DELIMITER ;
call alterTableCity();
Drop procedure IF EXISTS alterTableCity; 

Drop procedure IF EXISTS alterMediaassetImage; 
DELIMITER //
create procedure alterMediaassetImage() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'attribute_image' and DATA_TYPE='VARCHAR' 
     and TABLE_NAME = 'b_mediaasset_attributes'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_mediaasset_attributes CHANGE COLUMN `attribute_image` `attribute_image` VARCHAR(150) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call alterMediaassetImage();
Drop procedure IF EXISTS alterMediaassetImage; 

insert ignore into  m_permission VALUES (null,'organization','CREATE_NEWSALE','NEWSALE','CREATE',0);
insert ignore into  m_permission VALUES (null,'organization','CREATE_SECONDSALE','SECONDSALE','CREATE',0);
insert ignore into  m_permission VALUES (null,'organization','CREATE_DEVICERENTAL','DEVICERENTAL','CREATE',0);

