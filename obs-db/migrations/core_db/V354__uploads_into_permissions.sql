INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_HARDWAREITEMS', 'HARDWAREITEMS', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CUSTOMERACTIVATION', 'CUSTOMERACTIVATION', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_TRANSFER', 'TRANSFER', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CUSTOMERREGISTRATION', 'CUSTOMERREGISTRATION', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_ADJUSTMENTS', 'ADJUSTMENTS', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_PAYMENTSUPLOAD', 'PAYMENTSUPLOAD', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_MRNUPLOAD', 'MRNUPLOAD', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_MOVEITEMSALE', 'MOVEITEMSALE', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_EPG', 'EPG', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_MEDIAASSETS', 'MEDIAASSETS', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_ORGANIZATIONUPLOADS', 'ORGANIZATIONUPLOADS', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_ADDPLAN', 'ADDPLAN', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CHANGEPLAN', 'CHANGEPLAN', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CANCELPLAN', 'CANCELPLAN', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CLIENTSERVICESUSPEND', 'CLIENTSERVICESUSPEND', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CLIENTSERVICEREACTIVE', 'CLIENTSERVICEREACTIVE', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_MRNCARTON', 'MRNCARTON', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CREATELEAD', 'CREATELEAD', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_SERVICEACTIVATION', 'SERVICEACTIVATION', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_SERVICEPLANACTIVATION', 'SERVICEPLANACTIVATION', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CLIENTSERVICETERMINATION', 'CLIENTSERVICETERMINATION', 'READ', '1');
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('Uploads', 'READ_CREATEUSER', 'CREATEUSER', 'READ', '1');

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`,`can_maker_checker`) VALUES ('Master/Others', 'READ_OFFICEPAYMENT', 'OFFICEPAYMENT', 'READ','1');

DELETE FROM `stretchy_report` WHERE report_name = 'Access Profile Report';

DELETE FROM `stretchy_report` WHERE report_name = 'operators under organization';

DELETE FROM `stretchy_report` WHERE report_name = 'All Stocks Report';

DELETE FROM `stretchy_report` WHERE report_name = 'List Of Users';

INSERT IGNORE into `stretchy_report` values(null, 'operators under organization', 'Table', 'Report','Organization','Select id,office_type,name,(Select count(0) from m_office b
where b.hierarchy like concat(\".\",a.id,\'%\')) entity_count from m_office a
where a.hierarchy like
\'${currentUserHierarchy}%\'','operators under organization', '1', '1');



