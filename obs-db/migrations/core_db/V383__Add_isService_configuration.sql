INSERT IGNORE INTO `c_configuration` (`name`, `enabled`, `value`, `module`, `description`) VALUES ('isService', '1', '{\"serviceType\":\"CATV\",\"serviceParamName\":\"Network_node\"}\n', 'ClientService', 'it is Used to Get Service Details');

delete from b_network_element where  system_code='none';

delete from b_service_detail where service_id=0;
