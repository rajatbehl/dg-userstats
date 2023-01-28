CREATE OR REPLACE PROCEDURE public.getuserdetails(userid uuid, INOUT resultdata character varying)
 LANGUAGE plpgsql
AS $procedure$
	declare
		response text;
		amount decimal;
		suspicious boolean;
		markedNotSuspiciousDate timestamp;
		
	begin
		select life_time_deposit_amount into amount from usercashstats where user_Id = userid;
		
		select marked_not_suspicious_date into markedNotSuspiciousDate from suspicious_users where user_Id = userid and is_suspicious = true;
		if(found) then
			if(markedNotSuspiciousDate is not null) then
				if(markedNotSuspiciousDate <= now() - interval '72 HOURS') then
					suspicious = true;
				else
					suspicious = false;
				end if;
			else
				suspicious = true;
			end if;
		else
			suspicious = false;
		end if;
	
		if(amount is null) then 
			amount = 0;
		end if;
	
		response = concat(amount, '_', suspicious);
		select response into resultdata;
    end;
$procedure$
;