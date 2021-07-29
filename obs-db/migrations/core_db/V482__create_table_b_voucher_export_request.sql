CREATE TABLE IF NOT exists `obstenant-default`.b_voucher_export_request(
`request_id` varchar(50) NOT NULL,
  `request_date` datetime NOT NULL,
  `status` varchar(15) NOT NULL,
  `quantity` bigint(10) NOT NULL,
  `sale_ref_no` bigint(20) NOT NULL,
 `request_by` bigint(20) NOT NULL,
  PRIMARY KEY (`request_id`)
) 

