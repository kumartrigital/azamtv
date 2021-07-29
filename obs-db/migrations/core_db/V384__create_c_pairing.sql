CREATE TABLE IF NOT EXISTS`c_pairing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item_id` int(20) NOT NULL,
  `paired_item_id` int(20) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






