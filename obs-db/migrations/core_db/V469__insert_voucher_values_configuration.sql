INSERT ignore INTO `c_configuration` (`name`,`enabled`,`value`,`module`,`description`)
VALUES ( 'Voucherpins', '0', '{
"enable_default":"Voucher",
"length_pin": "12",
"begin_with": "1",
"pin_category": "NUMERIC",
"length_serial": "12"}', 'Master', 'Default values for voucher');
