ALTER TABLE `m_office` 
ADD COLUMN `pancard_no` VARCHAR(20) NOT NULL AFTER `settlement_poId`,
ADD COLUMN `company_reg_no` VARCHAR(60) NOT NULL AFTER `pancard_no`,
ADD COLUMN `gst_reg_no` VARCHAR(20) NOT NULL AFTER `company_reg_no`,
ADD COLUMN `commision_model` INT(1) NOT NULL AFTER `gst_reg_no`;


Update m_currency set id=356 where id=64;
Update m_code set module='Client' where code_name='Preference';
Update m_code set module='Client' where code_name='Property Type';
Update m_code set module='Depricated' where code_name in ('Gender','Education_cd','GroupClosureReason');


