CREATE OR REPLACE PROCEDURE public.fetchusercashstatsinbulk(user_ids uuid[], INOUT resultdata text[])
 LANGUAGE plpgsql
AS $procedure$
	declare 
		usercashstatsrecord record ;
	    userscashgameinfo text[] := array[] :: text[];
	
	begin
		create temporary table user_ids_temp(user_id uuid);
		insert into user_ids_temp(user_id) select unnest(user_ids);
	
		for usercashstatsrecord in 
		select ucs.user_id, ucs.first_game_played_name, ucs.games_tried, ucs.life_time_deposit_amount, ucs.life_time_withdrawl_amount, ucs.life_time_rake, ucs.life_time_games_won, ucs.life_time_games_lost  
		from usercashstats ucs
		inner join user_ids_temp uit on ucs.user_id = uit.user_id 
		
		loop
	    	userscashgameinfo = array_append(userscashgameinfo, concat(usercashstatsrecord.user_id, '_', usercashstatsrecord.first_game_played_name, '_', usercashstatsrecord.games_tried, '_', usercashstatsrecord.life_time_deposit_amount, '_', usercashstatsrecord.life_time_withdrawl_amount, '_', usercashstatsrecord.life_time_rake, '_', usercashstatsrecord.life_time_games_won + usercashstatsrecord.life_time_games_lost) :: text);
		end loop;
	
		resultdata := userscashgameinfo;
		drop table user_ids_temp;
	end;
$procedure$
;