CREATE TABLE IF NOT EXISTS `b_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_code` varchar(15) DEFAULT NULL,
  `product_description` varchar(100) DEFAULT NULL,
  `product_category` char(1) NOT NULL,
  `service_id` bigint(10) NOT NULL,
  `provision_id` bigint(20) NOT NULL,  
  `validity_start` date DEFAULT NULL,
  `validity_end` date DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `is_deleted` char(1) DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `product_code_key` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert ignore into m_permission values(null,'portfolio','CREATE_PRODUCT','PRODUCT','CREATE',0);
insert ignore into m_permission values(null,'portfolio','READ_PRODUCT','PRODUCT','READ',0);
insert ignore into m_permission values(null,'portfolio','UPDATE_PRODUCT','PRODUCT','UPDATE',0);
insert ignore into m_permission values(null,'portfolio','DELETE_PRODUCT','PRODUCT','DELETE',0);
