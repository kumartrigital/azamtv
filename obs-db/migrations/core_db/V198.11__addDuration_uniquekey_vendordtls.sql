Drop procedure IF EXISTS addDurationToUniqueKey;
DELIMITER //
create procedure addDurationToUniqueKey() 
Begin
  IF  EXISTS (
     SELECT * FROM information_schema. KEY_COLUMN_USAGE
     WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_vendor_agmt_detail'
      and CONSTRAINT_NAME = 'bvad_uq3')THEN

alter table `b_vendor_agmt_detail` DROP KEY `bvad_uq3`, ADD UNIQUE `bvad_uq3`(`vendor_agmt_id`,`content_code`,`loyalty_type`,`duration_id`);
END IF;
END //
DELIMITER ;
call addDurationToUniqueKey();
Drop procedure IF EXISTS addDurationToUniqueKey;
