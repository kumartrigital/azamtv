

insert ignore into m_permission values(null,'Masters','DELETE_CURRENCY','CURRENCY','DELETE',0);


insert ignore into m_permission values(null,'Masters','UPDATE_CURRENCYS','CURRENCY','UPDATE',0);



Drop procedure IF EXISTS currencydeleted;
DELIMITER //
create procedure currencydeleted()
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_deleted'
     and TABLE_NAME = 'm_currency'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_currency`
ADD COLUMN `is_deleted` CHAR(1) NOT NULL DEFAULT 'N' AFTER `type`;

END IF;
END //
DELIMITER ;
call currencydeleted();
Drop procedure IF EXISTS currencydeleted;
