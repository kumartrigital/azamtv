Drop procedure IF EXISTS salesPlanCategoryId;
DELIMITER //
create procedure salesPlanCategoryId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'sales_plan_category_id'
     and TABLE_NAME = 'b_sales_cataloge'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_sales_cataloge` 
ADD COLUMN `sales_plan_category_id` BIGINT(20) NOT NULL AFTER `name`;
END IF;
END //
DELIMITER ;
call salesPlanCategoryId();
Drop procedure IF EXISTS salesPlanCategoryId;




INSERT IGNORE INTO `m_code` (`id`, `code_name`, `is_system_defined`, `code_description`) VALUES (null, 'Sales Category', '0', 'defination of Sales Category');
SET @a_sc:=(select id from m_code where code_name='Sales Category');
INSERT INTO `m_code_value` (`id`, `code_id`, `code_value`, `order_position`) VALUES (null,@a_sc, 'Base Pack', '0');
INSERT INTO `m_code_value` (`id`, `code_id`, `code_value`, `order_position`) VALUES (null, @a_sc, 'Alcarte Pack', '1');
INSERT INTO `m_code_value` (`id`, `code_id`, `code_value`, `order_position`) VALUES (null, @a_sc, 'Addons Pack', '2');
INSERT INTO `m_code_value` (`id`, `code_id`, `code_value`, `order_position`) VALUES (null, @a_sc, 'Hardware Pack', '3');

