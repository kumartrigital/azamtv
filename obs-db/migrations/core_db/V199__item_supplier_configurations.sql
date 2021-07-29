INSERT IGNORE INTO `c_configuration` (`name`, `enabled`) VALUES ('item-supplier-mapping', '0');

ALTER TABLE `b_item_master` 
ADD COLUMN `supplier_id` BIGINT(5) NULL DEFAULT NULL AFTER `warranty`;

