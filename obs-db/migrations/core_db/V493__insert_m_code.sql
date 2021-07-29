INSERT INTO m_code (code_name,is_system_defined,code_description,module)
SELECT * FROM (SELECT ("Voucher Value"),0,"List values applicable for voucher creation","Voucher") AS tmp
WHERE NOT EXISTS (
SELECT code_name FROM m_code WHERE code_name = "Voucher Value"
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value,order_position)
SELECT * FROM (SELECT (select id from m_code where code_name="Voucher Value"), "100", 0) AS tmp
WHERE NOT EXISTS (
SELECT code_value FROM m_code_value WHERE code_value = "100"
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value,order_position)
SELECT * FROM (SELECT (select id from m_code where code_name="Voucher Value"), "200", 1) AS tmp
WHERE NOT EXISTS (
SELECT code_value FROM m_code_value WHERE code_value = "200"
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value,order_position)
SELECT * FROM (SELECT (select id from m_code where code_name="Voucher Value"), "300", 2) AS tmp
WHERE NOT EXISTS (
SELECT code_value FROM m_code_value WHERE code_value = "300"
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value,order_position)
SELECT * FROM (SELECT (select id from m_code where code_name="Voucher Value"), "400", 3) AS tmp
WHERE NOT EXISTS (
SELECT code_value FROM m_code_value WHERE code_value = "400"
) LIMIT 1;

INSERT INTO m_code_value (code_id, code_value,order_position)
SELECT * FROM (SELECT (select id from m_code where code_name="Voucher Value"), "500", 4) AS tmp
WHERE NOT EXISTS (
SELECT code_value FROM m_code_value WHERE code_value = "500"
) LIMIT 1;
