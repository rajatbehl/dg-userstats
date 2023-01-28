CREATE OR REPLACE PROCEDURE public.addfrauduser(userid uuid, INOUT resultdata character varying)
 LANGUAGE plpgsql
AS $procedure$
	declare
		tally int;
	
	begin
		select count(*) into tally from suspicious_users where user_id = userid;
		
		if tally = 0 then
			insert into suspicious_users(user_id, is_suspicious, is_fraud, marked_fraud_date, fraud_reason, updated_at)
			values(userid, true, true, now(), 'Marked from admin', now());
		else
			update suspicious_users
			set is_suspicious = true, is_fraud = true, marked_fraud_date = now(), fraud_reason = 'Marked from admin', updated_at = now()
			where user_id = userid;
		end if;
	
		select 'SUCCESS' into resultdata;
    end;
$procedure$
;