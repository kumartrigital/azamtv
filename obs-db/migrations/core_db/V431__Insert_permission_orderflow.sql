
SET SQL_SAFE_UPDATES = 0;
UPDATE b_eventaction_mapping SET event_name='Create Order Workflow' WHERE event_name='Create Order';
SET SQL_SAFE_UPDATES = 1;





INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`) VALUES ('Service_Order', 'CREATE_OPERATIONS', 'OPERATIONS', 'CREATE');

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`) VALUES ('Service_Order', 'CREATE_SURVEYING', 'SURVEYING', 'CREATE');

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`) VALUES ('Service_Order', 'CREATE_INSTALLATIONS', 'INSTALLATIONS', 'CREATE');



