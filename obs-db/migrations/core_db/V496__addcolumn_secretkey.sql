ALTER TABLE `obstenant-default`.`m_appuser` 
ADD COLUMN `secret_key` VARCHAR(20) NULL AFTER `enabled`,
ADD COLUMN `secret_key_expiry_time` DATE NULL AFTER `secret_key`,
ADD COLUMN `secret_key_status` TINYINT NULL AFTER `secret_key_expiry_time`;

Create index idx_client_resourceID ON b_client_balance(resource_id);
Create index idx_id ON b_item_detail(id);
Create index idx_itemid ON b_item_master(id);









