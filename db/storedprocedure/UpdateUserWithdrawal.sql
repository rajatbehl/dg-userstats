CREATE OR REPLACE PROCEDURE public.updateuserwithdrawalinfo(userid uuid, amount double precision, withdrawalfulfilldate timestamp without time zone,  withdrawalStatus int, manualProcessedTime timestamp without time zone)
 LANGUAGE plpgsql
AS $procedure$
	declare
	firstWithdrawalDate TIMESTAMP;
		
	begin
		select first_withdrawal_date into firstWithdrawalDate from usercashstats where user_Id = userId;
	 
		if (withdrawalStatus = 2) then --FULFILLED
	 		if (firstWithdrawalDate is null) then
	 			update usercashstats 
	 			set first_withdrawal_date = withdrawalFulfillDate, last_withdrawal_date = withdrawalFulfillDate, 
	 			first_withdrawal_amount = amount, last_withdrawal_amount = amount,
	 			life_time_withdrawl_amount = life_time_withdrawl_amount + amount, 
	 			life_time_withdrawal_count = life_time_withdrawal_count + 1, updated_at = withdrawalFulfillDate 
	 			where user_Id = userId;
	 		else
	 			update usercashstats 
	 			set last_withdrawal_date = withdrawalFulfillDate, last_withdrawal_amount = amount,
	 			life_time_withdrawl_amount = life_time_withdrawl_amount + amount, 
	 			life_time_withdrawal_count = life_time_withdrawal_count + 1, updated_at = withdrawalFulfillDate
	 			where user_id = userId;
	 		end if;
	 	elsif (withdrawalStatus = 5) then --PENDING
	 		update usercashstats 
	 		set pending_withdrawal_amount = pending_withdrawal_amount + amount,
	 		updated_at = now() 
	 		where user_Id = userId;
	 	elsif (withdrawalStatus = 3) then --FAILED
	 		update usercashstats
	 		set pending_withdrawal_amount = pending_withdrawal_amount - amount,
	 		updated_at = now() 
	 		where user_Id = userId;
	 	else -- MANUAL APPROVAL
	 		update usercashstats
	 		set last_manual_withdrawal_processed_time = manualProcessedTime,
	 		updated_at = now() 
	 		where user_Id = userId;
		end if;
    END;
$procedure$
;