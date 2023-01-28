CREATE OR REPLACE PROCEDURE public.addsuspicioususer(userid uuid, ispnlruleviolated boolean, isgameplaypnlruleviolated boolean, ismanualwithdrawalinlasthours boolean, isDepositFailureRuleViolated boolean)
 LANGUAGE plpgsql
AS $procedure$
	declare
		tally int;
	begin
		select count(*) into tally from suspicious_users where user_id = userid;
		
		if(tally = 0) then
			insert into suspicious_users(user_id, is_suspicious, profit_and_loss, game_play_profit_and_loss, is_manual_withdrawal_in_last_hours, deposit_failure, updated_at)
			values(userid, true, ispnlruleviolated, isgameplaypnlruleviolated, ismanualwithdrawalinlasthours, isDepositFailureRuleViolated, now());
		else
			update suspicious_users
			set is_suspicious = true,
			profit_and_loss = ispnlruleviolated,
			game_play_profit_and_loss = isgameplaypnlruleviolated,
			is_manual_withdrawal_in_last_hours = ismanualwithdrawalinlasthours,
			deposit_failure = isDepositFailureRuleViolated,
			updated_at = now()
			where user_id = userid;
		end if;
    end;
$procedure$
;