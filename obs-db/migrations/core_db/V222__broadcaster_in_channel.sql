ALTER TABLE `b_channel` 
ADD COLUMN `broadcaster_id` BIGINT(20) NOT NULL AFTER `channel_sequence`,
ADD INDEX `fk_bchnl_broadcstrid` (`broadcaster_id` ASC);
ALTER TABLE `b_channel` 
ADD CONSTRAINT `fk_bchnl_broadcstrid`
  FOREIGN KEY (`broadcaster_id`)
  REFERENCES `b_broadcaster` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

