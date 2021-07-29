CREATE TABLE if not existS  `b_vendor_management` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `vendor_code` varchar(20) NOT NULL,
  `vendor_name` varchar(100) DEFAULT NULL,
  `vendor_contact_name` varchar(100) NOT NULL,
  `vendor_emailid` varchar(100) NOT NULL,
  `vendor_mobile` int(20) NOT NULL,
  `vendor_landline` int(20) DEFAULT NULL,
  `vendor_address` varchar(200) DEFAULT NULL,
  `vendor_country` int(10) NOT NULL,
  `vendor_currency` varchar(20) NOT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uvendor_code_key` (`vendor_code`),
  UNIQUE KEY `uvendor_mobileno_key` (`vendor_mobile`),
  UNIQUE KEY `uvendor_emailid_key` (`vendor_emailid`),
  UNIQUE KEY `uvendor_landlineno_key` (`vendor_landline`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

CREATE TABLE  if not existS  `b_vendor_agreement` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `vendor_id` int(20) NOT NULL,
  `vendor_agmt_status` varchar(20) DEFAULT NULL,
  `vendor_agmt_startdate` datetime DEFAULT NULL,
  `vendor_agmt_enddate` datetime DEFAULT NULL,
  `content_type` varchar(20) NOT NULL,
  `vendor_agmt_document` varchar(500) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;



CREATE TABLE if not existS  `b_vendor_agmt_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `vendor_agmt_id` int(20) NOT NULL,
  `content_code` varchar(10) NOT NULL,
  `loyalty_type` varchar(20) NOT NULL,
  `loyalty_share` decimal(11,2) DEFAULT '0.00',
  `price_region` varchar(30) NOT NULL,
  `content_cost` decimal(10,2) DEFAULT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bvad_uq3` (`vendor_agmt_id`,`content_code`,`loyalty_type`),
  KEY `FK_Ven_agmt_Master` (`vendor_agmt_id`),
  CONSTRAINT `FK_Ven_agmt_Master` FOREIGN KEY (`vendor_agmt_id`) REFERENCES `b_vendor_agreement` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

INSERT IGNORE INTO  m_permission VALUES(null,'vendormanagement', 'CREATE_VENDORMANAGEMENT', 'VENDORMANAGEMENT', 'CREATE', '0');
INSERT IGNORE INTO  m_permission VALUES(null,'vendormanagement', 'UPDATE_VENDORMANAGEMENT', 'VENDORMANAGEMENT', 'UPDATE', '0');
INSERT IGNORE INTO  m_permission VALUES(null,'vendormanagement', 'DELETE_VENDORMANAGEMENT', 'VENDORMANAGEMENT', 'DELETE', '0');

INSERT IGNORE INTO  m_permission VALUES(null,'vendoragreement', 'CREATE_VENDORAGREEMENT', 'VENDORAGREEMENT', 'CREATE', '0');
INSERT IGNORE INTO  m_permission VALUES(null,'vendoragreement', 'UPDATE_VENDORAGREEMENT', 'VENDORAGREEMENT', 'UPDATE', '0');
