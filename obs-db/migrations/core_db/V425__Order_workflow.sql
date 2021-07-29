CREATE TABLE if not exists `b_order_workflow` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT(20) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  `assigned_to` BIGINT(20) NOT NULL,
  `description` TEXT NULL,
  `is_deleted` CHAR(1) NULL DEFAULT 'N',
  PRIMARY KEY (`id`));

INSERT ignore INTO `m_code` (`code_name`, `is_system_defined`, `code_description`, `module`) VALUES ('Service Custom Status', '0', 'Order Workflow Status', 'Client');


SET @m_mcid=(select id from m_code where code_name='Service Custom Status');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mcid, 'Draft', '0');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES ( @m_mcid, 'Order Accepted', '1');
INSERT ignore INTO`m_code_value` ( `code_id`, `code_value`, `order_position`) VALUES ( @m_mcid, 'Survey Done', '2');
INSERT ignore INTO `m_code_value` ( `code_id`, `code_value`, `order_position`) VALUES (@m_mcid, 'Installation Done', '3');
INSERT ignore INTO `m_code_value` ( `code_id`, `code_value`, `order_position`) VALUES ( @m_mcid, 'Provision', '4');


INSERT ignore INTO `b_eventaction_mapping` (`event_name`, `action_name`, `process`, `order_by`, `pre_post`, `process_params`, `is_deleted`, `is_synchronous`) VALUES ('Create Order', 'Activation_request', 'workflow_events', '1', 'N', 'Status_change', 'Y', 'Y');


INSERT ignore INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_ORDERWORKFLOW', 'ORDERWORKFLOW', 'CREATE', '1');

