
ALTER TABLE `b_tax_mapping_rate` 
ADD COLUMN `end_date` DATETIME NULL DEFAULT NULL AFTER `tax_region_id`,
ADD COLUMN `is_deleted` CHAR(1) NOT NULL DEFAULT 'N' AFTER `end_date`;






