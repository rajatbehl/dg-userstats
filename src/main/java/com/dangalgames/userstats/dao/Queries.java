package com.dangalgames.userstats.dao;

public final class Queries {

	private Queries() {

	}

	static final String INSERT_USER_REGISTRATION = "insert into usercashstats(user_Id) values(?) ";

	static final String UPDATE_USER_GAME_CASH_STATS = "call updateusercashstatsinfo(?,?,?,?,?,?,?)";

	static final String UPDATE_DEPOSIT = "call updateuserdepositinfo(?,?,?,?)";

	static final String UPDATE_WITHDRAWALS = "call updateuserwithdrawalinfo(?,?,?,?,?)";
	
	static final String GET_USER_GAME_COUNT = "select life_time_games_won + life_time_games_lost as gameCount from usercashstats u where user_id = ?";
	
	static final String GET_DEPOSIT_DETAILS = "select life_time_deposit_amount, life_time_deposit_count from usercashstats where user_id = ?";
	
	static final String GET_WITHDRAWAL_DETAILS = "select life_time_withdrawl_amount, life_time_withdrawal_count from usercashstats where user_id =?";

	static final String GET_LAST_DEPOSIT_DETAILS = "select last_deposit_amount, last_deposit_date from usercashstats where user_id =?";
	
	static final String GET_LAST_GAME_DETAILS = "select last_game_played_name, last_game_played_result, last_game_played from usercashstats where user_id =?";
	
	static final String SAVE_AND_GET_USER_STATS = "call saveandgetuserstats(?,?)"; 
	
	static final String UPDATE_GAMES_TRIED = "update usercashstats set games_tried=?, updated_at=? where user_id = ?";
	
	static final String UPDATE_USER_BALANCE = "update usercashstats set deposit_balance = ?, withdrawable_balance = ?, updated_at = ? where user_id = ?";
	
	static final String GET_USER_DETAILS_TO_CHECK_SUSPICIOUS = "select life_time_deposit_amount, pending_withdrawal_amount,life_time_rake, last_manual_withdrawal_processed_time,life_time_deposit_count, life_time_attempted_deposit_count from usercashstats where user_id = ?";
	
	static final String ADD_SUSPICIOUS_USER = "call addsuspicioususer(?,?,?,?,?)";
	
	static final String SET_SUSPICIOUS_USER_FRAUD = "update suspicious_users set is_fraud = ?, marked_fraud_date = ?, fraud_reason = ?, updated_at = ? where user_id = ?";
	
	static final String SET_SUSPICIOUS_USER_NOT_FRAUD = "update suspicious_users set is_suspicious = ?, is_fraud = ?, marked_not_suspicious_date = ?, updated_at = ? where user_id = ?";
	
	static final String GET_USER_DETAILS = "call getuserdetails(?,?)";
	
	static final String ADD_FRAUD_USER = "call addfrauduser(?,?)";

	static final String UPDATE_DEPOSIT_INITIATED_INFO = "update usercashstats set life_time_attempted_deposit_count = life_time_attempted_deposit_count + 1, updated_at=? where user_id = ?";
	
	static final String FETCH_LIFE_TIME_RUMMY_RAKE = "select life_time_rummy_rake from usercashstats where user_id = ?";
	
	static final String FETCH_USER_CASH_STATS_IN_BULK = "call fetchusercashstatsinbulk(?,?)";
	
	static final String UPDATE_DAILY_LOGIN_STREAK = "update usercashstats set daily_login_streak = ?, updated_at = now() where user_id = ?";
	
	static final String FETCH_USER_LOGIN_STREAK = "select daily_login_streak from usercashstats where user_id = ?";
			
}