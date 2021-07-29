CREATE TABLE  IF NOT EXISTS `obstenant-default`.`b_revpay_order` (
`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
`client_id` BIGINT(20) NOT NULL,
`action` VARCHAR(10) NULL,
`transaction_status` INT NULL,
`flwref` VARCHAR(45) NULL,
`tx_id` VARCHAR(45) NULL,
`amount` DOUBLE NULL,
`status` VARCHAR(45) NULL,
`purchase_type` VARCHAR(45) NULL,
`stb_no` VARCHAR(45) NULL,
`ref_id` VARCHAR(45) NULL,
`voucher_no` VARCHAR(45) NULL,
`created_at` DATE NULL,
`updated_at` DATE NULL,
PRIMARY KEY (`id`));



