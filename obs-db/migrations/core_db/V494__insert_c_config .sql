INSERT INTO c_configuration (name,enabled,value,module,description)
SELECT * FROM (SELECT ("Office_Entity"),1,'inview','Office','By using this configuration we can define office name default') AS tmp
WHERE NOT EXISTS (
SELECT name FROM c_configuration WHERE name = "Office_Entity"
) LIMIT 1;

INSERT INTO `obstenant-default`.`stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `selectOne`, `type`) 
VALUES ('pinNoType', 'pinNo', 'pinNo', 'text', 'varchar', '0', 'Y', 'Report');
