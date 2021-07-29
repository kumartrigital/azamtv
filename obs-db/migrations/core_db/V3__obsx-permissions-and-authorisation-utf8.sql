
-- ========= roles and permissions =========

/*
this scripts removes all current m_role_permission and m_permission entries
and then inserts new m_permission entries and just one m_role_permission entry
which gives the role (id 1 - super user) an ALL_FUNCTIONS permission

If you had other roles set up with specific permissions you will have to set up their permissions again.
*/

-- truncate `m_role_permission`;
-- truncate `m_permission`;
-- truncate `x_registered_table`;

INSERT INTO `m_permission`
(`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`) VALUES 
('special','ALL_FUNCTIONS',NULL,NULL,0),
('special','ALL_FUNCTIONS_READ',NULL,NULL,0),
('special', 'CHECKER_SUPER_USER', NULL, NULL, '0'),
('special','REPORTING_SUPER_USER',NULL,NULL,0),
('authorisation','READ_PERMISSION','PERMISSION','READ',0),
('authorisation','PERMISSIONS_ROLE','ROLE','PERMISSIONS',1),
('authorisation','CREATE_ROLE','ROLE','CREATE',1),
('authorisation','CREATE_ROLE_CHECKER','ROLE','CREATE',0),
('authorisation','READ_ROLE','ROLE','READ',0),
('authorisation','UPDATE_ROLE','ROLE','UPDATE',1),
('authorisation','UPDATE_ROLE_CHECKER','ROLE','UPDATE',0),
('authorisation','DELETE_ROLE','ROLE','DELETE',1),
('authorisation','DELETE_ROLE_CHECKER','ROLE','DELETE',0),
('authorisation','CREATE_USER','USER','CREATE',1),
('authorisation','CREATE_USER_CHECKER','USER','CREATE',0),
('authorisation','READ_USER','USER','READ',0),
('authorisation','UPDATE_USER','USER','UPDATE',1),
('authorisation','UPDATE_USER_CHECKER','USER','UPDATE',0),
('authorisation','DELETE_USER','USER','DELETE',1),
('authorisation','DELETE_USER_CHECKER','USER','DELETE',0),
('configuration','READ_CONFIGURATION','CONFIGURATION','READ',1),
('configuration','UPDATE_CONFIGURATION','CONFIGURATION','UPDATE',1),
('configuration','UPDATE_CONFIGURATION_CHECKER','CONFIGURATION','UPDATE',0),
('configuration','READ_CODE','CODE','READ',0),
('configuration','CREATE_CODE','CODE','CREATE',1),
('configuration','CREATE_CODE_CHECKER','CODE','CREATE',0),
('configuration','UPDATE_CODE','CODE','UPDATE',1),
('configuration','UPDATE_CODE_CHECKER','CODE','UPDATE',0),
('configuration','DELETE_CODE','CODE','DELETE',1),
('configuration','DELETE_CODE_CHECKER','CODE','DELETE',0),
('configuration', 'READ_CODEVALUE', 'CODEVALUE', 'READ', '0'),
('configuration', 'CREATE_CODEVALUE', 'CODEVALUE', 'CREATE', '1'),
('configuration', 'CREATE_CODEVALUE_CHECKER', 'CODEVALUE', 'CREATE', '0'),
('configuration', 'UPDATE_CODEVALUE', 'CODEVALUE', 'UPDATE', '1'),
('configuration', 'UPDATE_CODEVALUE_CHECKER', 'CODEVALUE', 'UPDATE', '0'),
('configuration', 'DELETE_CODEVALUE', 'CODEVALUE', 'DELETE', '1'),
('configuration', 'DELETE_CODEVALUE_CHECKER', 'CODEVALUE', 'DELETE', '0'),
('configuration','READ_CURRENCY','CURRENCY','READ',0),
('configuration','UPDATE_CURRENCY','CURRENCY','UPDATE',1),
('configuration','UPDATE_CURRENCY_CHECKER','CURRENCY','UPDATE',0),
('configuration', 'UPDATE_PERMISSION', 'PERMISSION', 'UPDATE', '1'),
('configuration', 'UPDATE_PERMISSION_CHECKER', 'PERMISSION', 'UPDATE', '0'),
('configuration', 'READ_DATATABLE', 'DATATABLE', 'READ', '0'),
('configuration', 'REGISTER_DATATABLE', 'DATATABLE', 'REGISTER', '1'),
('configuration', 'REGISTER_DATATABLE_CHECKER', 'DATATABLE', 'REGISTER', '0'),
('configuration', 'DEREGISTER_DATATABLE', 'DATATABLE', 'DEREGISTER', '1'),
('configuration', 'DEREGISTER_DATATABLE_CHECKER', 'DATATABLE', 'DEREGISTER', '0'),
('configuration', 'READ_AUDIT', 'AUDIT', 'READ', '0'),
('configuration', 'CREATE_CALENDAR', 'CALENDAR', 'CREATE', '0'),
('configuration', 'READ_CALENDAR', 'CALENDAR', 'READ', '0'),
('configuration', 'UPDATE_CALENDAR', 'CALENDAR', 'UPDATE', '0'),
('configuration', 'DELETE_CALENDAR', 'CALENDAR', 'DELETE', '0'),
('configuration', 'CREATE_CALENDAR_CHECKER', 'CALENDAR', 'CREATE', '0'),
('configuration', 'UPDATE_CALENDAR_CHECKER', 'CALENDAR', 'UPDATE', '0'),
('configuration', 'DELETE_CALENDAR_CHECKER', 'CALENDAR', 'DELETE', '0'),
('organisation', 'READ_MAKERCHECKER', 'MAKERCHECKER', 'READ', '0'),
('organisation', 'READ_CHARGE', 'CHARGE', 'READ', '0'),
('organisation', 'CREATE_CHARGE', 'CHARGE', 'CREATE', '1'),
('organisation', 'CREATE_CHARGE_CHECKER', 'CHARGE', 'CREATE', '0'),
('organisation', 'UPDATE_CHARGE', 'CHARGE', 'UPDATE', '1'),
('organisation', 'UPDATE_CHARGE_CHECKER', 'CHARGE', 'UPDATE', '0'),
('organisation', 'DELETE_CHARGE', 'CHARGE', 'DELETE', '1'),
('organisation', 'DELETE_CHARGE_CHECKER', 'CHARGE', 'DELETE', '0'),
('organisation', 'READ_OFFICE', 'OFFICE', 'READ', '0'),
('organisation', 'CREATE_OFFICE', 'OFFICE', 'CREATE', '1'),
('organisation', 'CREATE_OFFICE_CHECKER', 'OFFICE', 'CREATE', '0'),
('organisation', 'UPDATE_OFFICE', 'OFFICE', 'UPDATE', '1'),
('organisation', 'UPDATE_OFFICE_CHECKER', 'OFFICE', 'UPDATE', '0'),
('organisation', 'READ_OFFICETRANSACTION', 'OFFICETRANSACTION', 'READ', '0'),
('organisation', 'DELETE_OFFICE_CHECKER', 'OFFICE', 'DELETE', '0'),
('organisation', 'CREATE_OFFICETRANSACTION', 'OFFICETRANSACTION', 'CREATE', '1'),
('organisation', 'CREATE_OFFICETRANSACTION_CHECKER', 'OFFICETRANSACTION', 'CREATE', '0'),
('organisation', 'DELETE_OFFICETRANSACTION', 'OFFICETRANSACTION', 'DELETE', 1),
('organisation', 'DELETE_OFFICETRANSACTION_CHECKER', 'OFFICETRANSACTION', 'DELETE', 0),
('organisation', 'READ_STAFF', 'STAFF', 'READ', '0'),
('organisation', 'CREATE_STAFF', 'STAFF', 'CREATE', '1'),
('organisation', 'CREATE_STAFF_CHECKER', 'STAFF', 'CREATE', '0'),
('organisation', 'UPDATE_STAFF', 'STAFF', 'UPDATE', '1'),
('organisation', 'UPDATE_STAFF_CHECKER', 'STAFF', 'UPDATE', '0'),
('organisation', 'DELETE_STAFF', 'STAFF', 'DELETE', '1'),
('organisation', 'DELETE_STAFF_CHECKER', 'STAFF', 'DELETE', '0'),
('portfolio', 'READ_CLIENT', 'CLIENT', 'READ', '0'),
('portfolio', 'CREATE_CLIENT', 'CLIENT', 'CREATE', '1'),
('portfolio', 'CREATE_CLIENT_CHECKER', 'CLIENT', 'CREATE', '0'),
('portfolio', 'UPDATE_CLIENT', 'CLIENT', 'UPDATE', '1'),
('portfolio', 'UPDATE_CLIENT_CHECKER', 'CLIENT', 'UPDATE', '0'),
('portfolio', 'DELETE_CLIENT', 'CLIENT', 'DELETE', '1'),
('portfolio', 'DELETE_CLIENT_CHECKER', 'CLIENT', 'DELETE', '0'),
('portfolio', 'READ_CLIENTIMAGE', 'CLIENTIMAGE', 'READ', '0'),
('portfolio', 'CREATE_CLIENTIMAGE', 'CLIENTIMAGE', 'CREATE', '1'),
('portfolio', 'CREATE_CLIENTIMAGE_CHECKER', 'CLIENTIMAGE', 'CREATE', '0'),
('portfolio', 'DELETE_CLIENTIMAGE', 'CLIENTIMAGE', 'DELETE', '1'),
('portfolio', 'DELETE_CLIENTIMAGE_CHECKER', 'CLIENTIMAGE', 'DELETE', '0'),
('portfolio', 'READ_CLIENTNOTE', 'CLIENTNOTE', 'READ', '0'),
('portfolio', 'CREATE_CLIENTNOTE', 'CLIENTNOTE', 'CREATE', '1'),
('portfolio', 'CREATE_CLIENTNOTE_CHECKER', 'CLIENTNOTE', 'CREATE', '0'),
('portfolio', 'UPDATE_CLIENTNOTE', 'CLIENTNOTE', 'UPDATE', '1'),
('portfolio', 'UPDATE_CLIENTNOTE_CHECKER', 'CLIENTNOTE', 'UPDATE', '0'),
('portfolio', 'DELETE_CLIENTNOTE', 'CLIENTNOTE', 'DELETE', '1'),
('portfolio', 'DELETE_CLIENTNOTE_CHECKER', 'CLIENTNOTE', 'DELETE', '0'),
('portfolio', 'READ_GROUPNOTE', 'GROUPNOTE', 'READ', '0'),
('portfolio', 'CREATE_GROUPNOTE', 'GROUPNOTE', 'CREATE', '1'),
('portfolio', 'UPDATE_GROUPNOTE', 'GROUPNOTE', 'UPDATE', '1'),
('portfolio', 'DELETE_GROUPNOTE', 'GROUPNOTE', 'DELETE', '1'),
('portfolio', 'CREATE_GROUPNOTE_CHECKER', 'GROUPNOTE', 'CREATE', '0'),
('portfolio', 'UPDATE_GROUPNOTE_CHECKER', 'GROUPNOTE', 'UPDATE', '0'),
('portfolio', 'DELETE_GROUPNOTE_CHECKER', 'GROUPNOTE', 'DELETE', '0'),
('portfolio', 'READ_SAVINGNOTE', 'SAVINGNOTE', 'READ', '0'),
('portfolio', 'CREATE_SAVINGNOTE', 'SAVINGNOTE', 'CREATE', '1'),
('portfolio', 'UPDATE_SAVINGNOTE', 'SAVINGNOTE', 'UPDATE', '1'),
('portfolio', 'DELETE_SAVINGNOTE', 'SAVINGNOTE', 'DELETE', '1'),
('portfolio', 'CREATE_SAVINGNOTE_CHECKER', 'SAVINGNOTE', 'CREATE', '0'),
('portfolio', 'UPDATE_SAVINGNOTE_CHECKER', 'SAVINGNOTE', 'UPDATE', '0'),
('portfolio', 'DELETE_SAVINGNOTE_CHECKER', 'SAVINGNOTE', 'DELETE', '0'),
('portfolio', 'READ_CLIENTIDENTIFIER', 'CLIENTIDENTIFIER', 'READ', '0'),
('portfolio', 'CREATE_CLIENTIDENTIFIER', 'CLIENTIDENTIFIER', 'CREATE', '1'),
('portfolio', 'CREATE_CLIENTIDENTIFIER_CHECKER', 'CLIENTIDENTIFIER', 'CREATE', '0'),
('portfolio', 'UPDATE_CLIENTIDENTIFIER', 'CLIENTIDENTIFIER', 'UPDATE', '1'),
('portfolio', 'UPDATE_CLIENTIDENTIFIER_CHECKER', 'CLIENTIDENTIFIER', 'UPDATE', '0'),
('portfolio', 'DELETE_CLIENTIDENTIFIER', 'CLIENTIDENTIFIER', 'DELETE', '1'),
('portfolio', 'DELETE_CLIENTIDENTIFIER_CHECKER', 'CLIENTIDENTIFIER', 'DELETE', '0'),
('portfolio', 'READ_DOCUMENT', 'DOCUMENT', 'READ', '0'),
('portfolio', 'CREATE_DOCUMENT', 'DOCUMENT', 'CREATE', '1'),
('portfolio', 'CREATE_DOCUMENT_CHECKER', 'DOCUMENT', 'CREATE', '0'),
('portfolio', 'UPDATE_DOCUMENT', 'DOCUMENT', 'UPDATE', '1'),
('portfolio', 'UPDATE_DOCUMENT_CHECKER', 'DOCUMENT', 'UPDATE', '0'),
('portfolio', 'DELETE_DOCUMENT', 'DOCUMENT', 'DELETE', '1'),
('portfolio', 'DELETE_DOCUMENT_CHECKER', 'DOCUMENT', 'DELETE', '0'),
('portfolio', 'READ_GROUP', 'GROUP', 'READ', '0'),
('portfolio', 'CREATE_GROUP', 'GROUP', 'CREATE', '1'),
('portfolio', 'CREATE_GROUP_CHECKER', 'GROUP', 'CREATE', '0'),
('portfolio', 'UPDATE_GROUP', 'GROUP', 'UPDATE', '1'),
('portfolio', 'UPDATE_GROUP_CHECKER', 'GROUP', 'UPDATE', '0'),
('portfolio', 'DELETE_GROUP', 'GROUP', 'DELETE', '1'),
('portfolio', 'DELETE_GROUP_CHECKER', 'GROUP', 'DELETE', '0'),
('portfolio', 'UNASSIGNSTAFF_GROUP', 'GROUP', 'UNASSIGNSTAFF', 1),
('portfolio', 'UNASSIGNSTAFF_GROUP_CHECKER', 'GROUP', 'UNASSIGNSTAFF', 0),
('portfolio', 'READ_GUARANTOR', 'GUARANTOR', 'READ', 0),
('portfolio', 'CREATE_GUARANTOR', 'GUARANTOR', 'CREATE', 1),
('portfolio', 'CREATE_GUARANTOR_CHECKER', 'GUARANTOR', 'CREATE', 0),
('portfolio', 'UPDATE_GUARANTOR', 'GUARANTOR', 'UPDATE', 1);


-- == accounting related permissions
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('accounting', 'CREATE_GLACCOUNT', 'GLACCOUNT', 'CREATE', 1),
('accounting', 'UPDATE_GLACCOUNT', 'GLACCOUNT', 'UPDATE', 1),
('accounting', 'DELETE_GLACCOUNT', 'GLACCOUNT', 'DELETE', 1),
('accounting', 'CREATE_GLCLOSURE', 'GLCLOSURE', 'CREATE', 1),
('accounting', 'UPDATE_GLCLOSURE', 'GLCLOSURE', 'UPDATE', 1),
('accounting', 'DELETE_GLCLOSURE', 'GLCLOSURE', 'DELETE', 1), 
('accounting', 'CREATE_JOURNALENTRY', 'JOURNALENTRY', 'CREATE', 1),
('accounting', 'REVERSE_JOURNALENTRY', 'JOURNALENTRY', 'REVERSE', 1);


INSERT INTO `m_role` (`id`, `name`, `description`) 
VALUES 
(1,'Super user','This role provides all application permissions.');

/* role 1 is super user, give it ALL_FUNCTIONS */
INSERT INTO m_role_permission(role_id, permission_id)
select 1, id
from m_permission
where code = 'ALL_FUNCTIONS';

INSERT INTO `m_appuser` (`id`, `office_id`, `username`, `firstname`, `lastname`, `password`, `email`, 
`firsttime_login_remaining`, `nonexpired`, `nonlocked`, `nonexpired_credentials`, `enabled`) 
VALUES 
(1,1,'mifos','App','Administrator','5787039480429368bf94732aacc771cd0a3ea02bcf504ffe1185ab94213bc63a','demomfi@mifos.org','\0','','','','');


INSERT INTO `m_appuser_role` (`appuser_id`, `role_id`) VALUES (1,1);


-- Add in permissions for any special datatables added in base reference data
-- This needs to always happen at end of the script

/* add a create, read, update and delete permission for each registered datatable */
insert into m_permission(grouping, `code`, entity_name, action_name)
select 'datatable', concat('CREATE_', r.registered_table_name), r.registered_table_name, 'CREATE'
from x_registered_table r;

insert into m_permission(grouping, `code`, entity_name, action_name)
select 'datatable', concat('READ_', r.registered_table_name), r.registered_table_name, 'READ'
from x_registered_table r;

insert into m_permission(grouping, `code`, entity_name, action_name)
select 'datatable', concat('UPDATE_', r.registered_table_name), r.registered_table_name, 'UPDATE'
from x_registered_table r;

insert into m_permission(grouping, `code`, entity_name, action_name)
select 'datatable', concat('DELETE_', r.registered_table_name), r.registered_table_name, 'DELETE'
from x_registered_table r;


/* regardless of inserted permission settings above, no permissions (transactions) are preselected as being part of the maker-checker process
so, just set the flag to false... the end-user can decide which permissions should be maker-checkerable
*/
update m_permission set can_maker_checker = false;