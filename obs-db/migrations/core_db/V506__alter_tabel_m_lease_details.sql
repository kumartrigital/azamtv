
ALTER TABLE `obstenant-default`.`b_lease_details` 
ADD COLUMN `device_id` VARCHAR(45) NULL AFTER `otp`,
ADD COLUMN `voucher_id` VARCHAR(45) NULL AFTER `device_id`,
ADD COLUMN `image_path` VARCHAR(45) NULL AFTER `voucher_id`;
