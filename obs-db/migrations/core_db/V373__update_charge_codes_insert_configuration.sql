INSERT IGNORE INTO b_contract_period (`contract_period`, `contract_duration`, `contract_type`, `is_deleted`) VALUES ('2 Months', '2', 'Month(s)', 'N');
INSERT IGNORE INTO b_charge_codes ( `charge_code`, `charge_description`, `charge_type`, `charge_duration`, `duration_type`, `tax_inclusive`, `billfrequency_code`, `is_aggregate`) VALUES ('BSC', 'BiMonthly Subscription', 'RC', '2', 'Month(s)', '0', '2 Months', '0');
Update `c_configuration` set `value`='{"billDayOfMonth":"1","billCurrency":"64","billFrequency":"2"}' where `name`='bill_profile';





