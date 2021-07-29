Drop procedure IF EXISTS purchaseProductId;
DELIMITER //
create procedure purchaseProductId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
    WHERE COLUMN_NAME = 'packageId'
     and TABLE_NAME = 'm_office_agreement_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_office_agreement_detail` 
ADD COLUMN `purchase_product_poId` VARCHAR(20) NULL DEFAULT NULL AFTER `billing_frequency`,
ADD COLUMN `package_id` VARCHAR(20) NULL DEFAULT NULL AFTER `purchase_product_poId`;
END IF;
END //
DELIMITER ;
call purchaseProductId();
Drop procedure IF EXISTS purchaseProductId;


