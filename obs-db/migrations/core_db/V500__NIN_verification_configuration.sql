INSERT IGNORE INTO `obstenant-default`.`m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Masters', 'VERIFY_NIN', 'NIN', 'VERIFY', '0');

INSERT IGNORE INTO `obstenant-default`.`c_configuration` (`name`, `enabled`, `value`, `module`, `description`) VALUES ('NINVerification', '1', 'true', 'NINVerification for activation', 'NINVerification for activation');

