insert ignore into m_permission VALUES (null,'billing','PROCESS_DATAUPLOADS','DATAUPLOADS','PROCESS',0);

update m_permission set code='CREATE_GROUPSPROVISION',entity_name='GROUPSPROVISION' where code='CREATE_PROVISION';

update m_permission set code='CREATE_GROUPSDETAILS',entity_name='GROUPSDETAILS' where code='CREATE_GROUPS';

update m_permission set code='READ_GROUPSDETAILS', entity_name='GROUPSDETAILS' where code='READ_GROUPS';

insert IGNORE into m_permission values(null, 'organisation', 'CREATE_VOUCHER', 'VOUCHER', 'CREATE', '0');
insert IGNORE into m_permission values(null, 'organisation', 'PROCESS_VOUCHER', 'VOUCHER', 'PROCESS', '1');
insert IGNORE into m_permission values(null, 'organisation', 'READ_VOUCHER', 'VOUCHER', 'READ', '1');

SET SQL_SAFE_UPDATES = 0;
SET foreign_key_checks = 0;
delete from m_permission where entity_name Like '%RANDOMGENERATOR%';
delete from m_permission where entity_name = 'PROSPECT';
insert IGNORE into c_configuration VALUES (null,'OSD_ProvisioningSystem',1,'Comvenient');

insert IGNORE into m_permission values(null, 'Crm', 'CREATE_PROSPECT', 'PROSPECT', 'CREATE', '0');
insert IGNORE into m_permission values(null, 'Crm', 'UPDATE_PROSPECT', 'PROSPECT', 'UPDATE', '1');
insert IGNORE into m_permission values(null, 'Crm', 'DELETE_PROSPECT', 'PROSPECT', 'DELETE', '2');
insert IGNORE into m_permission values(null, 'Crm', 'FOLLOWUP_PROSPECT', 'PROSPECT', 'FOLLOWUP', '3');
insert IGNORE into m_permission values(null, 'Crm', 'CONVERT_PROSPECT', 'PROSPECT', 'CONVERT', '4');
insert IGNORE into m_permission values(null, 'Crm', 'READ_PROSPECT', 'PROSPECT', 'READ', '5');
SET SQL_SAFE_UPDATES = 1;
SET foreign_key_checks = 1;