DELETE FROM `b_city` WHERE `city_name`='selfcare';

DELETE FROM `b_country` WHERE `country_name`='selfcare';

DELETE FROM `b_state` WHERE `state_name`='selfcare';

DELETE FROM `b_district` WHERE `district_name`='selfcare';

INSERT IGNORE INTO `c_configuration` (`name`,`enabled`,`value`,`module`,`description`) VALUES ('Register_City',1,'selfcare','Registration','regiser for city');



