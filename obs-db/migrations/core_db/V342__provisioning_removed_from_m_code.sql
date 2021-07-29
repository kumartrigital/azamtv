SET SQL_SAFE_UPDATES = 0;
DELETE FROM `m_code_value` where code_id in (select id FROM `m_code` WHERE `code_name`='Provisioning');
DELETE FROM `m_code` WHERE `code_name`='Provisioning';
--rename table b_Network_Element to b_network_element;
SET SQL_SAFE_UPDATES = 1;

