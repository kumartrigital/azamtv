CREATE TABLE  IF NOT EXISTS `b_mediator_detail` (
  `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
  `request_type` VARCHAR(30) NOT NULL,
  `request_message` TEXT NOT NULL,
  `response_message` TEXT NULL,
  `status` VARCHAR(1) NOT NULL DEFAULT 'N',
  `task_id` INT(1) NOT NULL,
  PRIMARY KEY (`id`))AUTO_INCREMENT=1 ENGINE=InnoDB DEFAULT CHARSET=utf8;




INSERT ignore INTO `c_configuration` (`id`, `name`, `enabled`, `value`, `module`, `description`) 
VALUES ('66', 'is-CRM-Enable', '0', 'Celcom', 'CRM', 'this is for C-crm Solution');

