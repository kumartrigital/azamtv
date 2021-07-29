 INSERT ignore  INTO `obstenant-default`.`job` 
(`name`,`display_name`,`cron_expression`,
`cron_description`,`create_time`,`task_priority`,`group_name`,
`previous_run_start_time`,`next_run_time`,`job_key`,`initializing_errorlog`,`is_active`,
`currently_running`,`updates_allowed`,`scheduler_group`,`is_misfired`,`user_id`) 
VALUES ('INACTIVECUSTLEASECHARGING','INACTIVECUSTLEASECHARGING','0 30 23 1/1 * ? *','Daily once at Midnight',
'2020-05-06 12:27:52',5,NULL,'2020-05-08 12:22:16','2020-05-08 12:23:00','INACTIVECUSTLEASECHARGINGJobDetaildefault _ DEFAULT',NULL,1,1,1,0,0,NULL); 
INSERT ignore INTO `obstenant-default`.`c_configuration` (`name`, `enabled`, `value`, `module`, `description`) VALUES ('inactiveCustomerWithLeaseSTB', '1', '', 'stb lease', 'configuration to check inactive customers with 30day subcription on hold');
INSERT ignore INTO `obstenant-default`.`c_configuration` (`name`, `enabled`, `value`, `module`, `description`) VALUES ('leaseStbPlanCode', '1', '{"stb_lease_plancode": "BOXLEASE"}', 'stb lease', 'configuration to give lease stb plan code');
INSERT ignore INTO `obstenant-default`.`c_configuration` (`name`, `enabled`, `value`, `module`, `description`) VALUES ('clientInactiveSince', '1', '{"duration":30}', 'stb lease', 'configuration to apply lease concept for how long');
INSERT ignore INTO `obstenant-default`.`c_configuration` ( `name`, `enabled`, `value`, `module`, `description`) VALUES ('leaseSTBMonthlyCharge', '0', '{"planCode":"LEASE200","planPrice":-200}', 'STB', 'flag lease charge functionality for service on hold'); 


