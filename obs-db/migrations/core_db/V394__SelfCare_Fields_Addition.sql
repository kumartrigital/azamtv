ALTER TABLE `b_clientuser` 
ADD COLUMN `auth_token` TEXT NULL DEFAULT NULL AFTER `zebra_subscriber_id`,
ADD COLUMN `latitude` VARCHAR(11) NULL DEFAULT NULL AFTER `auth_token`,
ADD COLUMN `longitude` VARCHAR(11) NULL DEFAULT NULL AFTER `latitude`;

