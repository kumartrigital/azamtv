INSERT IGNORE INTO b_country (`country_code`, `country_name`, `is_active`) VALUES ( 'selfcare', 'selfcare', 'Y');
 
SET @c_id=(select id from b_country where country_code ='selfcare');
INSERT IGNORE INTO b_state (`state_code`, `state_name`, `parent_code`, `is_delete`) VALUES ( 'selfcare', 'selfcare', @c_id, 'N');

SET @s_id=(select id from b_state where state_code ='selfcare');
INSERT IGNORE INTO b_district (`district_code`, `district_name`, `parent_code`, `is_delete`) VALUES ('selfcare', 'selfcare', @s_id, 'N');

SET @d_id=(select id from b_district where district_code ='selfcare');
INSERT IGNORE INTO b_city (`city_code`, `city_name`, `parent_code`, `is_delete`) VALUES ('selfcare', 'selfcare', @d_id, 'N');


