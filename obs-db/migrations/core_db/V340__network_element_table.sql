CREATE TABLE IF NOT EXISTS `b_Network_Element` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `system_code` VARCHAR(120) NOT NULL,
  `system_name` VARCHAR(45) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  `is_deleted` CHAR(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `system_code_UNIQUE` (`system_code` ASC)
);




INSERT INTO `m_permission` (`id`, `grouping`, `code`, `entity_name`, `action_name`) VALUES (null, 'Provisioning', 'CREATE_NETWORKELEMENT', 'NETWORKELEMENT', 'CREATE');

INSERT INTO `m_permission` (`id`, `grouping`, `code`, `entity_name`, `action_name`) VALUES (null, 'Provisioning', 'UPDATE_NETWORKELEMENT', 'NETWORKELEMENT', 'UPDATE');

INSERT INTO `m_permission` (`id`, `grouping`, `code`, `entity_name`, `action_name`) VALUES (null, 'Provisioning', 'DELETE_NETWORKELEMENT', 'NETWORKELEMENT','DELETE');

INSERT INTO `m_permission` (`id`, `grouping`, `code`, `entity_name`, `action_name`) VALUES (null, 'Provisioning', 'READ_NETWORKELEMENT', 'NETWORKELEMENT', 'READ');

