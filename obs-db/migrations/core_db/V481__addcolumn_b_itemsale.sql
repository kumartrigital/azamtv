ALTER TABLE `obstenant-default`.`b_itemsale` 
ADD COLUMN `charge_amount` DECIMAL(10,0) NULL AFTER `agent_id`,
ADD COLUMN `unit_price` DECIMAL(5,0) NULL AFTER `charge_amount`;

