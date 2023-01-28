CREATE OR REPLACE PROCEDURE public.saveandgetuserstats(userid uuid, INOUT resultdata character varying)
 LANGUAGE plpgsql
AS $procedure$
	declare
		lastGamePlayed character varying;
		lastGamePlayedTime timestamp;
		lastGameResult character varying;
		lastDeposit decimal;
		lastDepositTime timestamp;
		lastSeen timestamp;
		userCount integer;
		startOfYesterday timestamp;
		endOfYesterday timestamp;
		dailyLoginStreak integer;
	
	begin	 
		select count(*) into userCount from usercashstats where user_id = userId;
		
		if userCount = 0 then
			select 'USER_NOT_EXIST' into resultData;
		else
			select last_seen, daily_login_streak into lastSeen, dailyLoginStreak from usercashstats where user_id = userId;
	
			select date_trunc('day', now() - interval '1 day') into startOfYesterday;
			select date_trunc('day', now() - interval '1 day') + interval '1 day' - interval '1 millisecond' into endOfYesterday;
		
			if lastSeen >= startOfYesterday and lastSeen <= endOfYesterday then
				dailyLoginStreak = dailyLoginStreak + 1;
			elseif lastSeen < startOfYesterday then
				dailyLoginStreak = 1;
			end if;
		
			update usercashstats set updated_at = now(), last_seen=now(), daily_login_streak = dailyLoginStreak where user_id = userId;
		
			select last_game_played_name, last_game_played_result, last_game_played, last_deposit_amount, last_deposit_date, last_seen, daily_login_streak
			into lastGamePlayed, lastGameResult, lastGamePlayedTime, lastDeposit, lastDepositTime, lastSeen, dailyLoginStreak
			from usercashstats
			where user_id = userId;
		 
			select concat(lastGamePlayed, '|', lastGameResult, '|',  lastGamePlayedTime, '|', lastDeposit, '|', lastDepositTime, '|', lastSeen, '|', dailyLoginStreak) into resultData;
		end if;
    end;
$procedure$
;
