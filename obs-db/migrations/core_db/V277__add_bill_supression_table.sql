create table if not exists c_supression_profile (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`min_bill_value` BIGINT(20) NULL DEFAULT NULL,
	`no_of_cycle_suppress` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (ID)
);



Insert into c_supression_profile values(1,100,0);



UPDATE `c_configuration` SET `value`='{"billDayOfMonth":"1","billCurrency":"64", "billFrequency":"52"}' WHERE `id`='67';
