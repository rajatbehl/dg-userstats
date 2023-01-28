CREATE OR REPLACE PROCEDURE public.updateusercashstatsinfo(userid uuid, winnings double precision, rake double precision, updatedat timestamp without time zone, game character varying, is_tourney boolean, INOUT result character varying)
 LANGUAGE plpgsql
AS $procedure$
	declare
		firstGamePlayed TIMESTAMP;
		gamesTried varchar;
		lifeTimeGamesWon int;
		lifeTimeGamesLost int;
		lifeTimeWithdrawalAmount decimal;
		lifeTimeDepositAmount decimal;
		withdrawalBalance decimal;
		depositBalance decimal;
		isFirstGamePlayed text;
		lifeTimeRummyRake decimal = 0.0;
		lifeTimeCallBreakRake decimal = 0.0;
		lifeTimeRake decimal;
		lastGamePlayedResult character varying;
		rummyCashTourneyWinnings decimal = 0.0;
		pokerCashTourneyWinnings decimal = 0.0;
		
		begin
			if game = 'Rummy' then
				lifeTimeRummyRake = rake;
			elseif game = 'Callbreak' then
				lifeTimeCallBreakRake = rake;
			end if;
			
			if(winnings > 0) then
	 			update usercashstats set life_time_games_won = life_time_games_won + 1, life_time_amount_won = life_time_amount_won + winnings, life_time_rake = life_time_rake + rake, updated_at = updatedAt, last_game_played_name=game, last_game_played_result='won', life_time_rummy_rake = life_time_rummy_rake + lifeTimeRummyRake, life_time_callbreak_rake = life_time_callbreak_rake + lifeTimeCallBreakRake where user_id = userid;
	 		
	 			if is_tourney then
					if game = 'Rummy' then
						rummyCashTourneyWinnings = rummyCashTourneyWinnings + winnings;
					elseif game = 'Poker' then
						pokerCashTourneyWinnings = pokerCashTourneyWinnings + winnings;
					end if;
				
					perform from usercashgamestats where user_id = userid;
					if found then
						update usercashgamestats set life_time_rummy_tourney_winnings = life_time_rummy_tourney_winnings + rummyCashTourneyWinnings, life_time_poker_tourney_winnings = life_time_poker_tourney_winnings + pokerCashTourneyWinnings, updated_at = now() where user_id = userid;
					else
						insert into usercashgamestats (user_id, life_time_rummy_tourney_winnings, life_time_poker_tourney_winnings) values (userid, rummyCashTourneyWinnings, pokerCashTourneyWinnings);
					end if;
				end if;
			else
	 			update usercashstats set life_time_games_lost = life_time_games_lost + 1, life_time_amount_lost = life_time_amount_lost + ABS(winnings), life_time_rake = life_time_rake + rake, updated_at = updatedAt, last_game_played_name=game, last_game_played_result='lost', life_time_rummy_rake = life_time_rummy_rake + lifeTimeRummyRake, life_time_callbreak_rake = life_time_callbreak_rake + lifeTimeCallBreakRake where user_id = userid;
			end if;
	
			select first_games_played, games_tried, life_time_games_won, life_time_games_lost, life_time_withdrawl_amount, life_time_deposit_amount, withdrawable_balance, deposit_balance, life_time_rummy_rake, life_time_rake, last_game_played_result into firstGamePlayed, gamesTried, lifeTimeGamesWon, lifeTimeGamesLost, lifeTimeWithdrawalAmount, lifeTimeDepositAmount, withdrawalBalance, depositBalance, lifeTimeRummyRake, lifeTimeRake, lastGamePlayedResult from usercashstats where user_Id = userid;
			
			perform from usercashgamestats where user_id = userid;
			if found then	
				select life_time_rummy_tourney_winnings, life_time_poker_tourney_winnings into rummyCashTourneyWinnings, pokerCashTourneyWinnings from usercashgamestats where user_id = userid;
			end if;
			
			if(firstGamePlayed is null) then
	 			update usercashstats set first_games_played = updatedAt, last_game_played = updatedAt, first_game_played_name = game  where user_id = userid;
				isFirstGamePlayed := 'true';
			else
	 			update usercashstats set last_game_played = updatedAt where user_id = userid;
				isFirstGamePlayed := 'false'; 
			end if;
			
			select concat(isFirstGamePlayed, '|', gamesTried, '|', lifeTimeGamesWon, '|', lifeTimeGamesLost, '|', lifeTimeWithdrawalAmount, '|', lifeTimeDepositAmount, '|', withdrawalBalance, '|', depositBalance, '|', lifeTimeRummyRake, '|', lifeTimeRake, '|', lastGamePlayedResult, '|', rummyCashTourneyWinnings, '|', pokerCashTourneyWinnings) into result; 
		end;
$procedure$
;
