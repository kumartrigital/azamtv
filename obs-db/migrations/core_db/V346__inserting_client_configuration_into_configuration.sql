INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'deviceAgrementType', '0', 'SALE', 'Device', 'When we do Activation is it with Sale / Rental');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'clientListing', '0', '{}', 'Customer', 'You enable or disable the columns ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'payment', '0', 'false', 'Payment', 'When payment should be enabled ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'IsClientIndividual', '0', 'false', 'Customer', 'it defines if the Customer Individual/Corporate ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'date_format', '0', 'dd MMM yyy', 'Date Format', 'Date format on the Client ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'registrationListing', '0', '{"passport":"false"}', 'Listing', 'Used While Customer Registration ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'nationalId', '0', 'false', 'Customer', 'Mandatory National ID field ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'IsGridEnable', '0', 'true', 'Customer', 'it defines Customer list instalization ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'isAutoRenew', '0', 'true', 'Order', 'it defines Order renewal instalization ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'clientonlinecheck', '0', 'false', 'Customer', 'it defines Customer list online button ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'IPTV', '0', 'true', 'Customer', 'it defines Customer instalization ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'SubscriptionPayment', '0', 'false', 'Payment', 'Payment is towards Subscription ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'codeDefinitionLength', '0', '10', 'Master', 'Code length ');
INSERT ignore INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`)
VALUES ( 'orderActions', '0', '{"suspend":"true","disconnect":"true","applypromo":"true","reconnect":"true","cangeplan":"true","topup/renewal":"true","pairing":"true","addons":"true","ndeviceswap":"true","commandcenter":"true","terminate":"true","ipchange":"true"}', 'Order', 'it defines Order Action instalization ');
