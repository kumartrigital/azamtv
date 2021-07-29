Drop procedure IF EXISTS addwalletinclientbal;
DELIMITER //
create procedure addwalletinclientbal() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'wallet_amount'
     and TABLE_NAME = 'b_client_balance'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_client_balance add column wallet_amount decimal(24,4) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call addwalletinclientbal();
Drop procedure IF EXISTS addwalletinclientbal;

Drop procedure IF EXISTS addwalletcheckinpayment;
DELIMITER //
create procedure addwalletcheckinpayment() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_wallet_payment'
     and TABLE_NAME = 'b_payments'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_payments add column is_wallet_payment char(1) DEFAULT 'N';
END IF;
END //
DELIMITER ;
call addwalletcheckinpayment();
Drop procedure IF EXISTS addwalletcheckinpayment;


CREATE TABLE IF NOT EXISTS `b_jv_transactions` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(10) NOT NULL,
  `jv_date` datetime NOT NULL,
  `jv_description` varchar(50) NOT NULL,
  `ref_id` int(10) default NULL,
  `credit_amount` decimal(24,4) DEFAULT NULL,
  `debit_amount` decimal(24,4) DEFAULT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
  );

insert ignore into c_configuration values(null,'is-wallet-enable',0,'false');
