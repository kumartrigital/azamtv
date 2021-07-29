CREATE TABLE IF NOT EXISTS `m_client_billprofile` (
  `client_id` BIGINT(20) NOT NULL,
  `bill_day_of_month` BIGINT(20) NOT NULL,
  `bill_currency` BIGINT(20) NOT NULL,
  `bill_frequency` BIGINT(20) NOT NULL,
  `bill_segment` VARCHAR(45) NULL DEFAULT NULL,
  `next_bill_day` DATE NULL DEFAULT NULL,
  `last_bill_day` DATE NULL DEFAULT NULL,
  `last_bill_no` BIGINT(20) NULL DEFAULT NULL,
  `payment_type` BIGINT(20) NULL DEFAULT NULL,
  `bill_suppression_flag` CHAR(1) NULL DEFAULT NULL,
  `bill_suppression_id` BIGINT(20) NULL DEFAULT NULL,
  `first_bill` CHAR(1) NULL DEFAULT NULL,
  `hot_bill` CHAR(1) NULL DEFAULT NULL,
   PRIMARY KEY (`client_id`));



ALTER TABLE `b_channel`
CHANGE COLUMN `channel_category` `channel_category` VARCHAR(100) NOT NULL ;


Insert ignore into `c_configuration` (`id`,`name`,`enabled`,`value`,`module`,`description`)
values (67,'bill_profile',1,'{"billDayOfMonth":"1","billCurrency":"64",
"billFrequency":"52"}','client','Default values for Client Bill Profile');
