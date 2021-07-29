ALTER TABLE `b_hw_plan_mapping` 
DROP COLUMN `plan_code`,
DROP INDEX `uk_hwplnmapping_all` ,
ADD UNIQUE INDEX `uk_hwplnmapping_all` (`item_code` ASC, `provisioning_id` ASC);
