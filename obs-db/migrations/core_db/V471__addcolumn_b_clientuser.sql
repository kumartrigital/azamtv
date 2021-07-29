ALTER TABLE `obstenant-default`.`b_clientuser` 
ADD COLUMN `firsttime_login_remaining` BIGINT(1) NULL AFTER `auth_token`,
ADD COLUMN `nonexpired` BIGINT(1) NULL AFTER `firsttime_login_remaining`,
ADD COLUMN `nonlocked` BIGINT(1) NULL AFTER `nonexpired`,
ADD COLUMN `nonexpired_credentials` BIGINT(1) NULL AFTER `nonlocked`,
ADD COLUMN `enabled` BIGINT(1) NULL AFTER `nonexpired_credentials`;

