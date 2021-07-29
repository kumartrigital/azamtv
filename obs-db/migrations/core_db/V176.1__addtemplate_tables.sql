CREATE TABLE IF NOT EXISTS `b_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `text` longtext CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `entity` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_name` (`name`)
);

CREATE TABLE IF NOT EXISTS `b_template_b_template_mappers` (
  `b_template_id` bigint(20) NOT NULL,
  `mappers_id` bigint(20) NOT NULL,
  UNIQUE KEY `mappers_id` (`mappers_id`),
  KEY `mappers_id_2` (`mappers_id`),
  KEY `b_template_id` (`b_template_id`)
) ;

CREATE TABLE IF NOT EXISTS `b_template_mappers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mapperkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `mapperorder` int(11) DEFAULT NULL,
  `mappervalue` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT IGNORE INTO m_permission VALUES (null, 'administration', 'CREATE_TEMPLATE', 'TEMPLATE', 'CREATE', '0');
INSERT IGNORE INTO m_permission VALUES(null, 'administration', 'UPDATE_TEMPLATE', 'TEMPLATE', 'UPDATE', '0');
INSERT IGNORE INTO m_permission VALUES(null, 'administration', 'DELETE_TEMPLATE', 'TEMPLATE', 'DELETE', '0');

INSERT IGNORE INTO b_eventaction_mapping VALUES(null,'Create Payment','Create Payment','workflow_events','N','N');





