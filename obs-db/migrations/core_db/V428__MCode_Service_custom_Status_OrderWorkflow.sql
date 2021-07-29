SET @m_mcid=(select id from m_code where code_name='Service Custom Status');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mcid, 'Operation Review', '5');
INSERT ignore INTO `m_code_value` ( `code_id`, `code_value`, `order_position`) VALUES ( @m_mcid, 'Provisioned', '6');
INSERT ignore INTO `m_code_value` ( `code_id`, `code_value`, `order_position`) VALUES ( @m_mcid, 'Survey Completed', '8');
INSERT ignore INTO `m_code_value` ( `code_id`, `code_value`, `order_position`) VALUES ( @m_mcid, 'Installation Completed', '9');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mcid, 'Surveying', '10');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mcid, 'Installation Assigned', '11');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mcid, 'Reject', '12');
INSERT ignore INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@m_mcid, 'Processing', '13');




ALTER TABLE `obstenant-default`.`b_client_service` 
CHANGE COLUMN `status` `status` VARCHAR(45) NOT NULL ;

