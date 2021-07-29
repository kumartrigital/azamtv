DROP PROCEDURE IF EXISTS custom_validation;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `custom_validation`(p_userid INT,p_clientid INT,
jsonstr text,event_name VARCHAR(200), out err_code INT,out err_msg VARCHAR(4000))
SWL_return:
BEGIN
If event_name='Event Order' then
begin
 Declare veventid int;
 Declare fmttype varchar(10);
 Declare optype varchar(10); 
 Declare isnewplan varchar(10); 
 Declare vprice double(20,2);
 Declare cbalance double(20,2);
 SET @optype= common_schema.extract_json_value(jsonstr, '//optType');
 Select price into vprice from b_mod_pricing where event_id=veventid and format_type=fmttype and Opt_type=optype and is_deleted='N' ;
 SET @cbalance=IFNULL((select sum(balance_amount) as balance_amount from b_client_balance where client_id=p_clientid GROUP BY resource_id ),0);
if cbalance-vprice<0 then 
        SET err_code = 0;
        SET err_msg = '';
else
        SET err_code = 0;
        SET err_msg = '';
end if;

end;


ELSEIF event_name='Rental' then
begin

  Declare v_allow2 INT default 0;
 Select count(0) into v_allow2 from b_onetime_sale where client_id=3 and is_deleted = 'N';
if v_allow2 > 0 then 
      
        SET err_code = 0;
        SET err_msg = '';
        select concat("erro ",err_code);
else
        SET err_code = 0;
        SET err_msg  = '';
         
end if;

end;

ELSEIF event_name='Order Booking' then
   begin
   DECLARE v_status INT ;
   Declare v_allow INT default 0;
   Declare vofficetype INT ;
   Declare vcontractPeriod int;
   Declare vpaytermCode varchar(20);  
   SET err_code = 0;
   SET err_msg = NULL ;
   
   
Select count(0) into v_allow 
from b_orders where  client_id =3 and order_status =1 ;

if (v_allow>0 and  common_schema.extract_json_value(jsonstr,'//isNewplan') = 'true')  then
  SET err_code = 0;
  SET err_msg = '' ;  
else
  Select count(0) into v_allow 
  from b_orders where plan_id=common_schema.extract_json_value(jsonstr, '//planCode') and client_id =p_clientid and order_status =3;
  if (v_allow>0 and  common_schema.extract_json_value(jsonstr,'//isNewplan') = 'true')   then
    SET err_code = 0;
    SET err_msg = '' ;  
  else
  Select count(0) into v_allow 
 from b_orders where  client_id =3 and order_status =4;
  if v_allow>0 then
    SET err_code = 0;
    SET err_msg = '' ;  
  end if;
    end if; 
end if;
select concat("end ",err_code);
if (err_code <> 0) then LEAVE SWL_return;
    end if;
    end;
else
        SET err_code = 0;
        SET err_msg = '';
end if;
 
END
