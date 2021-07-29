INSERT ignore INTO `m_code` (`code_name`, `is_system_defined`, `code_description`, `module`) VALUES ('Content Provider', '0', 'Content Provider', 'Media');

SET @m_mid=(select id from m_code where code_name='Content Provider');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mid, 'Content-1', '0');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mid, 'Content-2', '1');



