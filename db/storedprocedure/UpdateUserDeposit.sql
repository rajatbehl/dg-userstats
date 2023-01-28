CREATE OR REPLACE PROCEDURE public.updateuserdepositinfo(userid uuid, depositamount double precision, depositfulfilldate TIMESTAMP, inout result boolean)
 LANGUAGE plpgsql
AS $procedure$
	declare
		firstDepositDate TIMESTAMP;
	
	begin	

		select first_deposit_date into firstDepositDate from usercashstats where user_Id = userId;
	 
	 	if (firstDepositDate is null) then
	 		update usercashstats set first_deposit_date = depositfulfilldate, last_deposit_date = depositfulfilldate,
	 		first_deposit_amount = depositAmount , last_deposit_amount = depositAmount,
	 		life_time_deposit_amount = life_time_deposit_amount + depositAmount, life_time_deposit_count = life_time_deposit_count + 1, updated_at = depositfulfilldate where user_Id = userId;                                  
	 		
	 		select true into result; 
		
	 	else
			update usercashstats set last_deposit_date = depositfulfilldate, last_deposit_amount = depositAmount, 
			life_time_deposit_amount = life_time_deposit_amount + depositAmount, life_time_deposit_count = life_time_deposit_count + 1, updated_at = depositfulfilldate where user_id = userId;                                              
			
			select false into result;	
		
		end if;
	
    END;
$procedure$
;
