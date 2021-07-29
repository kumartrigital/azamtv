alter table b_plan_pricing add column chargeOwner VARCHAR(45) NULL DEFAULT NULL  AFTER rounding_type;
alter  table b_order_price add column chargeOwner VARCHAR(45) NULL DEFAULT NULL  AFTER currency_id;
alter  table b_charge add column chargeOwner VARCHAR(45) NULL DEFAULT NULL  AFTER currency_id;
alter  table b_charge_tax add column chargeOwner VARCHAR(45) NULL DEFAULT NULL ;
alter  table b_grn add column item_amount BIGINT(10) NULL DEFAULT NULL  AFTER po_no;
alter  table b_grn add column order_status BIGINT(1) NULL DEFAULT NULL  AFTER item_amount;
