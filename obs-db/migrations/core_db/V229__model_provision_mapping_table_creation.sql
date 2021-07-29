CREATE TABLE IF NOT EXISTS `b_model_provision_mapping` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `provisioning_id` bigint(10) NOT NULL,
  `model_id` bigint(10) NOT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT 'N',
  `createdby_id` bigint(10) NOT NULL,
  `created_date` datetime NOT NULL,
  `lastmodifiedby_id` bigint(10) NOT NULL,
  `lastmodified_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_bmpmaping_prov_model` (`provisioning_id`,`model_id`)
) ENGINE=InnoDB auto_increment=1 DEFAULT  CHARSET=latin1;

insert ignore into m_permission values(null,'portfolio','CREATE_MODELPROVISIONMAPPING','MODELPROVISIONMAPPING','CREATE',0);
insert ignore into m_permission values(null,'portfolio','UPDATE_MODELPROVISIONMAPPING','MODELPROVISIONMAPPING','UPDATE',0);
insert ignore into m_permission values(null,'portfolio','READ_MODELPROVISIONMAPPING','MODELPROVISIONMAPPING','READ',0);
insert ignore into m_permission values(null,'portfolio','DELETE_MODELPROVISIONMAPPING','MODELPROVISIONMAPPING','DELETE',0);
