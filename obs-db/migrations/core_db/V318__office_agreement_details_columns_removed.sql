Drop procedure IF EXISTS shareTypeAndShareAmount;
DELIMITER //
create procedure shareTypeAndShareAmount() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'plan_id'
     and TABLE_NAME = 'm_office_agreement_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_office_agreement_detail` 
DROP COLUMN `share_type`,
DROP COLUMN `share_amount`,
DROP COLUMN `source`,
DROP INDEX `agreement_dtl_ai_ps_mc_uniquekey` ,
ADD UNIQUE INDEX `agreement_dtl_ai_ps_mc_uniquekey` (`agreement_id`,`plan_id`);
END IF;
END //
DELIMITER ;
call shareTypeAndShareAmount();
Drop procedure IF EXISTS shareTypeAndShareAmount;
