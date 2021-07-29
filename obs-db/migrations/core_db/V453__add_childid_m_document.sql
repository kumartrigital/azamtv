Drop procedure IF EXISTS addChildEntityId;
DELIMITER //
create procedure addChildEntityId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'child_entity_id'
     and TABLE_NAME = 'm_document'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `obstenant-default`.`m_document` 
ADD COLUMN `child_entity_id` BIGINT(20) NULL DEFAULT NULL AFTER `parent_entity_id`;
END IF;
END //
DELIMITER ;
call addChildEntityId();
Drop procedure IF EXISTS addChildEntityId;
