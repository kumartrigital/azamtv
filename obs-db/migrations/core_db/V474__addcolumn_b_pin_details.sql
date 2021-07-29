ALTER TABLE `obstenant-default`.`b_pin_details` 
ADD COLUMN `office_id` BIGINT(15) NULL AFTER `cancel_reason`,
ADD COLUMN `sale_ref_no` BIGINT(20) NULL AFTER `office_id`;

