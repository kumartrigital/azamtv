DROP view IF EXISTS  plan_orders_vw;

CREATE  view`plan_orders_vw` AS select 
`pm`.`plan_code` AS `PLAN_CODE`,`pm`.`plan_description` AS `PLAN DESCRIPTION`,
`s`.`product_code` AS `PRODUCT CODE`,
`o`.`transaction_type` AS `TRANSACTION TYPE`,
`o`.`billing_frequency` AS `BILLING FREQUENCY`,
count(`o`.`client_id`) AS `CLIENT COUNT`,`o`.`order_status` AS `ORDER STATUS`,
`o`.`contract_period` AS `CONTRACT PERIOD`,count(`ol`.`order_id`) AS `ORDER COUNT` 
from ((((`b_order_line` `ol` join `b_product` `s` on((`ol`.`product_id` = `s`.`id`))) 
join `b_plan_detail` `pd` on((`pd`.`product_id` = `s`.`id`))) join `b_plan_master` `pm` 
on((`pd`.`plan_id` = `pm`.`id`))) join `b_orders` `o` on(((`o`.`plan_id` = `pm`.`id`)
 and (`o`.`order_status` = 1) and (`ol`.`order_id` = `o`.`id`)))) 
where (`o`.`is_deleted` = 'n') group by `pm`.`plan_code`,`pm`.`plan_description`,
`o`.`transaction_type`,`pd`.`product_id`,`o`.`transaction_type`,
`o`.`billing_frequency`,`ol`.`service_status`;
