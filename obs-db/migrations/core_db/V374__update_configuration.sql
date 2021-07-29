SET SQL_SAFE_UPDATES = 0;
Update `b_charge_codes` set `billfrequency_code`='1 Month' where `charge_code`='MSC';
Update `b_charge_codes` set `billfrequency_code`='3 Months' where `charge_code`='QSC';
Update `b_charge_codes` set `billfrequency_code`='6 Months' where `charge_code`='HSC';
Update `b_charge_codes` set `billfrequency_code`='1 Year' where `charge_code`='YSC';
Update `b_charge_codes` set `billfrequency_code`='2 Months' where `charge_code`='BSC';
SET SQL_SAFE_UPDATES = 1;

