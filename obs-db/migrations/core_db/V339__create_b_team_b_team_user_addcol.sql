
insert ignore into m_permission values(null, 'CRM', 'CREATE_TICKETTEAM', 'TICKETTEAM', 'CREATE', 0);
insert ignore into m_permission values (null, 'CRM', 'UPDATE_TICKETTEAM', 'TICKETTEAM', 'UPDATE', 0);
insert ignore into m_permission values (null, 'CRM', 'DELETE_TICKETTEAM', 'TICKETTEAM', 'DELETE', 0);



insert ignore into m_permission values(null, 'CRM', 'CREATE_TICKETMAPPING', 'TICKETMAPPING', 'CREATE', 0);
insert ignore into m_permission values(null, 'CRM', 'UPDATE_TICKETMAPPING', 'TICKETMAPPING', 'UPDATE', 0);
insert ignore into m_permission values(null, 'CRM', 'DELETE_TICKETMAPPING', 'TICKETMAPPING', 'DELETE', 0);


CREATE TABLE `b_team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `team_code` varchar(15) DEFAULT NULL,
  `team_description` varchar(100) DEFAULT NULL,
  `team_category` char(1) NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_team_code` (`team_code`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;


CREATE TABLE if not exists`b_team_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `team_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `user_role` char(1) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `is_team_lead` char(1) DEFAULT 'N',
  `is_deleted` char(1) DEFAULT 'N',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_teamId_userId` (`team_id`,`user_id`,`is_deleted`));
  







