Drop procedure IF EXISTS addCodeModuleColumn;
DELIMITER //
create procedure addCodeModuleColumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'm_code'
     and TABLE_NAME = 'module'
     and TABLE_SCHEMA = DATABASE())THEN
alter table m_code add column module VARCHAR(15) NOT NULL AFTER `code_description`;

END IF;
END //
DELIMITER ;
call addCodeModuleColumn();
Drop procedure IF EXISTS addCodeModuleColumn;


Update m_code set module='Client' where id=1;
Update m_code set module='General' where id=5;
Update m_code set module='Ticketing' where id=7;
Update m_code set module='Ticketing' where id=8;
Update m_code set module='Master' where id=9;
Update m_code set module='Client' where id=10;
Update m_code set module='Payment' where id=11;
Update m_code set module='Finance' where id=12;
Update m_code set module='Master' where id=14;
Update m_code set module='Master' where id=15;
Update m_code set module='Master' where id=17;
Update m_code set module='Order' where id=18;
Update m_code set module='Order' where id=20;
Update m_code set module='Master' where id=21;
Update m_code set module='Master' where id=22;
Update m_code set module='Scheduler' where id=23;
Update m_code set module='Scheduler' where id=24;
Update m_code set module='Ticketing' where id=25;
Update m_code set module='Lead' where id=26;
Update m_code set module='Lead' where id=27;
Update m_code set module='Event_Action' where id=28;
Update m_code set module='Event_Action' where id=29;
Update m_code set module='Accounting' where id=31;
Update m_code set module='Accounting' where id=32;
Update m_code set module='Accounting' where id=33;
Update m_code set module='Accounting' where id=34;
Update m_code set module='Accounting' where id=35;
Update m_code set module='Client' where id=38;
Update m_code set module='Order' where id=40;
Update m_code set module='Provision' where id=41;
Update m_code set module='Order' where id=43;
Update m_code set module='Order' where id=44;
Update m_code set module='Custom' where id=45;
Update m_code set module='Reseller' where id=46;
Update m_code set module='Master' where id=47;
Update m_code set module='Ticketing' where id=48;
Update m_code set module='Master' where id=49;
Update m_code set module='Order' where id=50;
Update m_code set module='Master' where id=51;
Update m_code set module='Inventory' where id=56;
Update m_code set module='Inventory' where id=57;
Update m_code set module='Reseller' where id=59;
Update m_code set module='Reseller' where id=60;
Update m_code set module='Order' where id=61;
Update m_code set module='Voucher' where id=62;
Update m_code set module='Client' where id=63;
Update m_code set module='Fees' where id=68;
Update m_code set module='Property' where id=69;
Update m_code set module='Provision' where id=70;


