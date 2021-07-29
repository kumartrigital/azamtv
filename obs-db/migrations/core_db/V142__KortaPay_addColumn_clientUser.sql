Drop procedure IF EXISTS kortaPay; 
DELIMITER //
create procedure kortaPay() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'korta_token'
     and TABLE_NAME = 'b_clientuser'
     and TABLE_SCHEMA = DATABASE())THEN 
alter table b_clientuser Add column korta_token varchar(100) default null unique after unique_reference;
END IF;
END //
DELIMITER ;
call kortaPay();

Drop procedure IF EXISTS kortaPay;




