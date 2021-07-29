ALTER TABLE `b_hw_plan_mapping` 
ADD UNIQUE INDEX `uk_hwplnmapping_all` (`item_code` ASC, `plan_code` ASC, `provisioning_id` ASC);

