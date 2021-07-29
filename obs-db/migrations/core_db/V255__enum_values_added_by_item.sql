set @name ='item_class';

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@name, '1', 'STB', 'STB');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@name, '2', 'SC', 'SC');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@name, '3', 'Modem', 'Modem');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@name, '4', 'Prepaid Cards', 'Prepaid Cards');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@name, '5', 'Soft Charge', 'Soft Charge');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@name, '6', 'Events', 'Events');

set @unitName ='item_unit_type';

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@unitName, '1', 'Pieces', 'Pieces');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@unitName, '2', 'Meters', 'Meters');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@unitName, '3', 'Hours', 'Hours');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@unitName, '4', 'Days', 'Days');

INSERT IGNORE INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`) 
VALUES (@unitName, '5', 'Accessories', 'Accessories');




