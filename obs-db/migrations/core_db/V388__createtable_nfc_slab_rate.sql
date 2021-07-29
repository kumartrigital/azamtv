SET @sc_scid=(select id from m_code where code_name='plan Type');
INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES (@sc_scid, 'BuildYourPlan', '3');


CREATE TABLE if not exists `b_NFC_slab_rate` (
  `id` BIGINT(20) NOT NULL,
  `slab_from` VARCHAR(20) NOT NULL,
  `slab_to` VARCHAR(20) NOT NULL,
  `slab_rate` FLOAT(20) NOT NULL,
  `is_deleted` CHAR(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`))ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
