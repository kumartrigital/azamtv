Drop procedure IF EXISTS addVoucherPriceId;
DELIMITER //
create procedure addVoucherPriceId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'price_id'
     and TABLE_NAME = 'b_pin_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_pin_master add column price_id bigint(20) NOT NULL after pin_value;

END IF;
END //
DELIMITER ;
call addVoucherPriceId();
Drop procedure IF EXISTS addVoucherPriceId;


Drop procedure IF EXISTS addPinDetailsSaleDate;
DELIMITER //
create procedure addPinDetailsSaleDate() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'sale_date'
     and TABLE_NAME = 'b_pin_details'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_pin_details add column sale_date DATETIME NULL DEFAULT NULL  AFTER `status` ;

END IF;
END //
DELIMITER ;
call addPinDetailsSaleDate();
Drop procedure IF EXISTS addPinDetailsSaleDate;


CREATE TABLE IF NOT EXISTS `b_partner_settlement` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `partner_id` bigint(20) NOT NULL,
  `agreement_id` bigint(11) NOT NULL,
  `d_date` datetime DEFAULT NULL,
  `source_type` varchar(30) NOT NULL,
  `charge_amount` decimal(22,5) DEFAULT NULL,
  `commission_percentage` varchar(13)  DEFAULT NULL,
  `commission_amount` decimal(22,5) DEFAULT NULL,
  `net_amount` decimal(22,5) DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB  DEFAULT CHARSET=latin1;


