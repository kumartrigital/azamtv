Drop procedure IF EXISTS removeForeignKey;
DELIMITER //
create procedure removeForeignKey() 
Begin
  IF  EXISTS (
     SELECT * FROM information_schema. KEY_COLUMN_USAGE
     WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME ='b_allocation'
     and COLUMN_NAME ='serial_no' and CONSTRAINT_NAME = 'fk_idtls_srno')THEN

ALTER TABLE b_allocation DROP FOREIGN KEY `fk_idtls_srno` ;
END IF;
END //
DELIMITER ;
call removeForeignKey();
Drop procedure IF EXISTS removeForeignKey;
