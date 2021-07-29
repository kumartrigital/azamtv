ALTER TABLE `b_order_line` 
ADD COLUMN `purchase_product_poid` VARCHAR(45) NULL DEFAULT 'Null' AFTER `product_id`;

