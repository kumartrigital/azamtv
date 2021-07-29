SET SQL_SAFE_UPDATES=0;
-- discon_vw -- 
CREATE OR REPLACE VIEW `discon_vw` AS
    select distinct
        `mo`.`name` AS `BRANCH`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,           
		`ca`.`city` AS `CITY`,
        `ca`.`state` AS `STATE`,
        `ca`.`country` AS `COUNTRY`,
        `pm`.`plan_description` AS `PLAN`,
        `bcp`.`contract_period` AS `CONTRACT PERIOD`,
        `o`.`billing_frequency` AS `BILL FRQUENCY`,
        date_format(`o`.`active_date`, '%d-%m-%Y') AS `ACTIVATION DATE`,
        date_format(`oh`.`actual_date`, '%d-%m-%Y') AS `DISCONNECTION DATE`,
        (to_days(`oh`.`actual_date`) - to_days(`o`.`active_date`)) AS `NO OF DAYS`,
        truncate(sum(`op`.`price`), 2) AS `PRICE`,
        `o`.`disconnect_reason` AS `DISCONNECT REASON`,
        (select 
                max(`x`.`hw_serial_no`)
            from
                `b_association` `x`
            where
                ((`c`.`id` = `x`.`client_id`)
                    and (`x`.`is_deleted` = 'N')
                    and (`x`.`order_id` = `o`.`id`))) AS `DEVICE`
    from
        (((((((`m_client` `c`
        join `m_office` `mo` ON (`c`.`office_id` = `mo`.`id`))
        join `b_client_address` `ca` ON (`c`.`id` = `ca`.`client_id`
            and `ca`.`address_key` = 'PRIMARY'))
        join `b_orders` `o` ON (`c`.`id` = `o`.`client_id`))
        join `b_order_price` `op` ON (`o`.`id` = `op`.`order_id`))
        join `b_orders_history` `oh` ON (`o`.`id` = `oh`.`order_id`))
        left join `b_contract_period` `bcp` ON (`o`.`contract_period` = `bcp`.`id`))
        left join `b_plan_master` `pm` ON (`o`.`plan_id` = `pm`.`id`))
    where
        ((`o`.`id` = (select 
                `o2`.`id`
            from
                `b_orders` `o2`
            where
                ((`o2`.`client_id` = `o`.`client_id`)
                    and (`o2`.`user_action` = 'DISCONNECTION')
                    and (`o`.`id` = `o2`.`id`))))
            and (`oh`.`id` = (select 
                max(`oh2`.`id`)
            from
                `b_orders_history` `oh2`
            where
                ((`oh2`.`transaction_type` = 'DISCONNECTION')
                    and (`oh2`.`order_id` = `oh`.`order_id`)))))
    group by `o`.`id`;

-- renewal_vw --

CREATE OR REPLACE VIEW `renewal_vw` AS
    select 
        `mo`.`name` AS `BRANCH`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `ca`.`city` AS `CITY`,
        `ca`.`state` AS `STATE`,
        `ca`.`country` AS `COUNTRY`,
        `pm`.`plan_description` AS `PLAN`,
        date_format(`oh`.`transaction_date`, '%d-%m-%Y') AS `RENEWAL DATE`,
        date_format(`o`.`active_date`, '%d-%m-%Y') AS `ACTIVATION DATE`,
        `o`.`billing_frequency` AS `BILL FRQUENCY`,
        truncate(sum(`op`.`price`), 2) AS `PRICE`,
        `u`.`username` AS `USER NAME`
    from
        (((((((`m_client` `c`
        join `m_office` `mo` ON (`c`.`office_id` = `mo`.`id`))
        join `b_client_address` `ca` ON (`c`.`id` = `ca`.`client_id`
            and `ca`.`address_key` = 'PRIMARY'))
        join `b_orders` `o` ON (`c`.`id` = `o`.`client_id`))
        join `b_order_price` `op` ON (`o`.`id` = `op`.`order_id`))
        join `b_orders_history` `oh` ON (`o`.`id` = `oh`.`order_id`))
        join `m_appuser` `u` ON (`oh`.`createdby_id` = `u`.`id`))
        left join `b_plan_master` `pm` ON (`o`.`plan_id` = `pm`.`id`))
    where
        (`oh`.`transaction_type` in ('RENEWAL AFTER AUTOEXIPIRY' , 'RENEWAL BEFORE AUTOEXIPIRY', 'Renewal'))
    group by `o`.`id`
    order by `oh`.`id` desc;

-- recon_vw --

CREATE OR REPLACE VIEW `recon_vw` AS
    select 
        `mo`.`name` AS `BRANCH`,
        `c`.`account_no` AS `ACCOUNT NO`,
        `c`.`display_name` AS `CLIENT NAME`,
        `ca`.`city` AS `CITY`,
        `ca`.`state` AS `STATE`,
        `ca`.`country` AS `COUNTRY`,
        `pm`.`plan_description` AS `PLAN`,
        date_format(`o`.`active_date`, '%d-%m-%Y') AS `ACTIVATION DATE`,
        date_format(`h2`.`actual_date`, '%d-%m-%Y') AS `DISCONNECT DATE`,
        date_format(`oh`.`actual_date`, '%d-%m-%Y') AS `RECONNECTION DATE`,
        `o`.`billing_frequency` AS `BILL FRQUENCY`,
        truncate(sum(`op`.`price`), 2) AS `PRICE`
    from
        (((((((`m_client` `c`
        join `m_office` `mo` ON (`c`.`office_id` = `mo`.`id`))
        join `b_client_address` `ca` ON (`c`.`id` = `ca`.`client_id`
            and `ca`.`address_key` = 'PRIMARY'))
        join `b_orders` `o` ON (`c`.`id` = `o`.`client_id`))
        join `b_order_price` `op` ON (`o`.`id` = `op`.`order_id`))
        join `b_orders_history` `oh` ON (`o`.`id` = `oh`.`order_id`))
        left join `b_plan_master` `pm` ON (`o`.`plan_id` = `pm`.`id`))
        left join `b_orders_history` `h2` ON (`o`.`id` = `h2`.`order_id`))
    where
        ((`o`.`id` = (select 
                `o2`.`id`
            from
                `b_orders` `o2`
            where
                ((`o2`.`client_id` = `o`.`client_id`)
                    and (`o2`.`user_action` = 'RECONNECTION')
                    and (`o`.`id` = `o2`.`id`))))
            and `oh`.`id` = (select 
                max(`oh2`.`id`)
            from
                `b_orders_history` `oh2`
            where
                (`oh2`.`transaction_type` = 'RECONNECTION')
                    and (`oh2`.`order_id` = `oh`.`order_id`))
            and `h2`.`id` = (select 
                max(`h3`.`id`)
            from
                `b_orders_history` `h3`
            where
                (`h3`.`transaction_type` = 'DISCONNECTION')
                    and (`h3`.`order_id` = `h2`.`order_id`)))
    group by `o`.`id`;

SET @ID=(select id from stretchy_report where report_name='List of Renewals');

UPDATE stretchy_report SET report_sql='select * FROM renewal_vw rv where 
((select o.id from m_office o where o.name=rv.BRANCH) = ''${officeId}'' or -1 = ''${officeId}'') and 
date_format(str_to_date( `RENEWAL DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  
between ''${startDate}'' and ''${endDate}'''
WHERE report_name='List of Renewals'; 

INSERT IGNORE INTO stretchy_report_parameter VALUES(null, @ID, '5', 'officeId');

UPDATE stretchy_report SET report_sql='select * FROM recon_vw rv where 
((select o.id from m_office o where o.name=rv.BRANCH) = ''${officeId}'' or -1 = ''${officeId}'') and 
date_format(str_to_date(`RECONNECTION DATE`,''%d-%m-%Y''),''%Y-%m-%d'')  
between ''${startDate}'' and ''${endDate}''',use_report=1
WHERE report_name='List of Re-Connections'; 

ALTER TABLE b_prov_service_details MODIFY provision_system VARCHAR(200);
ALTER TABLE b_process_request MODIFY provisioing_system VARCHAR(200);


