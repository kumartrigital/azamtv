CREATE TABLE IF NOT EXISTS `b_login_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(200) DEFAULT NULL,
  `device_id` varchar(200) DEFAULT NULL,
  `session_id` varchar(200) DEFAULT NULL,
  `username` varchar(20) NOT NULL,
  `login_time` datetime DEFAULT NULL,
  `logout_time` datetime DEFAULT NULL,
  `status` varchar(10) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=396 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci';
insert ignore into b_eventaction_mapping VALUES (null,'Event Order','Provision IT','Workflow_events','N','N');
