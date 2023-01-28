package com.dangalgames.userstats.dao;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.config.JdbcTemplateProvider;
import com.dangalgames.userstats.enums.Result;
import com.dangalgames.userstats.enums.SuspiciousUserAction;
import com.dangalgames.userstats.events.UpdateBalanceEvent;
import com.dangalgames.userstats.model.DepositEvent;
import com.dangalgames.userstats.model.DepositInitiatedEvent;
import com.dangalgames.userstats.model.LastDepositDetails;
import com.dangalgames.userstats.model.LastGameDetails;
import com.dangalgames.userstats.model.RegistrationEvent;
import com.dangalgames.userstats.model.SuspiciousUserDetails;
import com.dangalgames.userstats.model.UserGameCashStatsEvent;
import com.dangalgames.userstats.model.UserStats;
import com.dangalgames.userstats.model.WithdrawalEvent;
import com.dangalgames.userstats.model.request.MarkSuspiciousUserRequest;
import com.dangalgames.userstats.model.response.BulkUserCashStatsResponse;
import com.dangalgames.userstats.model.response.BulkUserCashStatsResponse.UserCashStats;
import com.dangalgames.userstats.model.response.DepositDetailsResponse;
import com.dangalgames.userstats.model.response.Response;
import com.dangalgames.userstats.model.response.UserDetailsResponse;
import com.dangalgames.userstats.model.response.WithdrawDetailsResponse;
import com.dangalgames.userstats.utils.Constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Async("db_executor")
public class UserStatsDaoImpl implements UserStatsDao {

	@Autowired
	private JdbcTemplateProvider jdbcTemplate;
	
	@Override
	@Async("db_executor")
	public void storeNewUser(RegistrationEvent registrationEvent) {
		try {
			int result = jdbcTemplate.getJdbcWriteTemplate().update(Queries.INSERT_USER_REGISTRATION, registrationEvent.getUserId());
			if (result != 1) {
				log.error("User Stats: DB Error while adding new user Id {}", registrationEvent);
			}
		} catch(DuplicateKeyException ex) {
			log.error("user {} already created with app launch event", registrationEvent.getUserId());
		} catch (DataAccessException ex) {
			log.error("User Stats: Error while adding new user Id {} {}", registrationEvent, ex);
		}
	}

	@Override
	public CompletableFuture<Boolean> storeUserDepositInfo(DepositEvent depositEvent) {
		DataSource dataSource = jdbcTemplate.getJdbcWriteTemplate().getDataSource();
		try (Connection connection = dataSource.getConnection();
				CallableStatement procedureQuery = connection.prepareCall(Queries.UPDATE_DEPOSIT)) {

			procedureQuery.setObject(1, UUID.fromString(depositEvent.getUserId()));
			procedureQuery.setDouble(2, depositEvent.getAmount());
			procedureQuery.setTimestamp(3, new Timestamp(depositEvent.getDepositFulfillDate()));
			procedureQuery.setBoolean(4, false);
			
			procedureQuery.execute();
			
			ResultSet resultSet = procedureQuery.getResultSet();
			boolean isFirstDeposit = false;
			if(resultSet.next()) {
				isFirstDeposit = resultSet.getBoolean(1);
			}
			
			return CompletableFuture.completedFuture(isFirstDeposit);
		} catch (SQLException e) {
			log.error("Error while updating user deposit info", e);
			return CompletableFuture.failedFuture(e);
		}
	}

	@Override
	public void storeWithdrawals(WithdrawalEvent withdrawalEvent) {
		DataSource dataSource = jdbcTemplate.getJdbcWriteTemplate().getDataSource();
		
		try (Connection connection = dataSource.getConnection();
				CallableStatement procedureQuery = connection.prepareCall(Queries.UPDATE_WITHDRAWALS)) {

			procedureQuery.setObject(1, withdrawalEvent.getUserId());
			procedureQuery.setDouble(2, withdrawalEvent.getAmount());
			procedureQuery.setTimestamp(3, new Timestamp(withdrawalEvent.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
			procedureQuery.setInt(4, withdrawalEvent.getWithdrawalStatus());
			procedureQuery.setTimestamp(5, withdrawalEvent.getManualProcessedTime() != null 
					? new Timestamp(withdrawalEvent.getManualProcessedTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) 
					: null);
			
			procedureQuery.execute();
			
		} catch (SQLException e) {
			log.error("Error while updating user withdrawal", e);
		}
	}

	@Override
	public CompletableFuture<String> storeAndGetGameCashStats(UserGameCashStatsEvent userGameCashStatsEvent) {

		DataSource dataSource = jdbcTemplate.getJdbcWriteTemplate().getDataSource();
		if(dataSource == null) {
			return CompletableFuture.completedFuture(Result.DB_ERROR.name());
		}
		try (Connection connection = dataSource.getConnection();
				CallableStatement procedureQuery = connection.prepareCall(Queries.UPDATE_USER_GAME_CASH_STATS)) {

			procedureQuery.setObject(1, UUID.fromString(userGameCashStatsEvent.getUserId()));
			procedureQuery.setDouble(2, userGameCashStatsEvent.getWinnings());
			procedureQuery.setDouble(3, userGameCashStatsEvent.getRake());
			procedureQuery.setTimestamp(4, new Timestamp(userGameCashStatsEvent.getCurrentTime()));
			procedureQuery.setString(5, userGameCashStatsEvent.getGame());
			procedureQuery.setBoolean(6, userGameCashStatsEvent.isTourney());
			procedureQuery.setString(7, Constant.FAILED);
			procedureQuery.execute();
			
			ResultSet resultSet = procedureQuery.getResultSet();
			String result = null;
			if(resultSet.next()) {
				result = resultSet.getString(1);
			}
			
			return CompletableFuture.completedFuture(result);
		} catch (SQLException e) {
			log.error("Error while updating user game cash stats", e);
			return CompletableFuture.failedFuture(e);
		}
	}

	@Override
	@Async("db_executor")
	public CompletableFuture<UserStats> getUserGameCount(String userId) {
		try {
			Map<String, Object> params = jdbcTemplate.getJdbcReadTemplate().queryForMap(Queries.GET_USER_GAME_COUNT, UUID.fromString(userId));
			return CompletableFuture.completedFuture(new UserStats(Result.SUCCESS, Integer.parseInt(String.valueOf(params.get("gameCount")))));
		} catch (DataAccessException ex) {
			log.error("Error while fetching user game count for UID {}", userId, ex);
			return CompletableFuture.completedFuture(new UserStats(Result.DB_ERROR));
		}
	}
	
	@Override
	@Async("db_executor")
	public CompletableFuture<DepositDetailsResponse> getDepositDetails(UUID userId) {
		try {
			Map<String, Object> params = jdbcTemplate.getJdbcReadTemplate().queryForMap(Queries.GET_DEPOSIT_DETAILS, userId);
			DepositDetailsResponse response = new DepositDetailsResponse(Result.SUCCESS);
			response.setTotalDepositAmount(params.get("life_time_deposit_amount") == null ? 0.0 : Double.parseDouble(String.valueOf(params.get("life_time_deposit_amount"))));
			response.setTotalDepositCount(params.get("life_time_deposit_count") == null ? 0 : Integer.parseInt(String.valueOf(params.get("life_time_deposit_count"))));
			
			return CompletableFuture.completedFuture(response);
		}catch(EmptyResultDataAccessException ex) {
			return CompletableFuture.completedFuture(new DepositDetailsResponse(Result.STATS_NOT_FOUND));
		}catch(Exception ex) {
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public CompletableFuture<WithdrawDetailsResponse> getWithdrawalDetails(UUID userId) {
		try {
			Map<String, Object> params = jdbcTemplate.getJdbcReadTemplate().queryForMap(Queries.GET_WITHDRAWAL_DETAILS, userId);
			WithdrawDetailsResponse response = new WithdrawDetailsResponse(Result.SUCCESS);
			response.setTotalWithdrawalAmount(params.get("life_time_withdrawl_amount") == null ? 0.0 :  Double.parseDouble(String.valueOf(params.get("life_time_withdrawl_amount"))));
			response.setTotalWithdrawalCount(params.get("life_time_withdrawal_count") == null ? 0 : Integer.parseInt(String.valueOf(params.get("life_time_withdrawal_count"))));
			
			return CompletableFuture.completedFuture(response);
		}catch(EmptyResultDataAccessException ex) {
			return CompletableFuture.completedFuture(new WithdrawDetailsResponse(Result.STATS_NOT_FOUND));
		}catch(Exception ex) {
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public CompletableFuture<LastDepositDetails> getLastDepositDetails(UUID userId) {
		try {
			Map<String, Object> params = jdbcTemplate.getJdbcReadTemplate().queryForMap(Queries.GET_LAST_DEPOSIT_DETAILS, userId);
			LastDepositDetails depositDetails = new LastDepositDetails();
			depositDetails.setLastDepositAmount(params.get("last_deposit_amount") == null ? null : String.valueOf(params.get("last_deposit_amount")));
			depositDetails.setLastDepositTime(params.get("last_deposit_date") == null ? null : String.valueOf(params.get("last_deposit_date")));
			
			return CompletableFuture.completedFuture(depositDetails);
		}catch(Exception ex) {
			log.error("Error while fetching last deposit details for UID {}", userId, ex);
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public CompletableFuture<LastGameDetails> getLastGameDetails(UUID userId) {
		try {
			Map<String, Object> params = jdbcTemplate.getJdbcReadTemplate().queryForMap(Queries.GET_LAST_GAME_DETAILS, userId);
			LastGameDetails details = new LastGameDetails();
			details.setLastGamePlayedTime(params.get("last_game_played") == null ? null : String.valueOf(params.get("last_game_played")));
			details.setLastGamePlayed(params.get("last_game_played_name") == null ? null : String.valueOf(params.get("last_game_played_name")));
			details.setLastGameResult(params.get("last_game_played_result") == null ? null : String.valueOf(params.get("last_game_played_result")));
			
			return CompletableFuture.completedFuture(details);
		}catch(Exception ex) {
			log.error("Error while fetching last game details for UID {}", userId, ex);
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public CompletableFuture<String> saveLastSeenAndGetUserStats(UUID userId) {
		DataSource dataSource = jdbcTemplate.getJdbcWriteTemplate().getDataSource();
		if(dataSource == null) {
			return CompletableFuture.completedFuture(Result.DB_ERROR.name());
		}
		try (Connection connection = dataSource.getConnection();
				CallableStatement procedureQuery = connection.prepareCall(Queries.SAVE_AND_GET_USER_STATS)) {

			procedureQuery.setObject(1, userId);
			procedureQuery.setString(2, Constant.FAILED);
			procedureQuery.execute();
			
			ResultSet resultSet = procedureQuery.getResultSet();
			String result = null;
			if(resultSet.next()) {
				result = resultSet.getString(1);
			}
			
			return CompletableFuture.completedFuture(result);
		} catch (SQLException e) {
			log.error("Error while getting user stats for UID {}", userId, e);
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@Override
	public void updateGamesTried(UUID userId, String gamesTried) {
		try {
			int updated = jdbcTemplate.getJdbcWriteTemplate().update(Queries.UPDATE_GAMES_TRIED, gamesTried, LocalDateTime.now(), userId);
			if(updated == 0)
				log.error("failed to update gamesTried {} for UID {}", gamesTried, userId);
		} catch(Exception ex) {
			log.error("Error while updating gamesTried {} for UID {}", gamesTried, userId, ex);
		}
	}
	
	@Override
	public void updateUserBalance(UpdateBalanceEvent event) {
		try {
			int updated = jdbcTemplate.getJdbcWriteTemplate().update(Queries.UPDATE_USER_BALANCE, event.getDepositBalance(), event.getWithdrawalBalance(), LocalDateTime.now(), event.getUserId());
			if(updated == 0)
				log.info("failed to update balance for userId: {}", event.getUserId());
		} catch(Exception ex) {
			log.error("error while updating balance for userId: {}", event.getUserId(), ex);
		}
	}

	@Override
	public CompletableFuture<SuspiciousUserDetails> getUserDetailsToCheckIfSuspicious(UUID userId) {
		try {
			Map<String, Object> params = jdbcTemplate.getJdbcWriteTemplate().queryForMap(Queries.GET_USER_DETAILS_TO_CHECK_SUSPICIOUS, userId);
			SuspiciousUserDetails userDetails = new SuspiciousUserDetails();
			
			userDetails.setTotalDepositAmount(params.get("life_time_deposit_amount") == null ? 0.0 : Double.parseDouble(String.valueOf(params.get("life_time_deposit_amount"))));
			userDetails.setTotalWithdrawalAmount(params.get("pending_withdrawal_amount") == null ? 0.0 : Double.parseDouble(String.valueOf(params.get("pending_withdrawal_amount"))));
			userDetails.setTotalRake(params.get("life_time_rake") == null ? 0.0 : Double.parseDouble(String.valueOf(params.get("life_time_rake"))));
			userDetails.setLastManualWithdrawalTime(params.get("last_manual_withdrawal_processed_time") == null ? null : Timestamp.valueOf(String.valueOf(params.get("last_manual_withdrawal_processed_time"))));
			userDetails.setLifetimeDepositCount(params.get("life_time_deposit_count") == null ? 0.0 : Double.parseDouble(String.valueOf(params.get("life_time_deposit_count"))));
			userDetails.setLifetimeAttemptedDepositCount(params.get("life_time_attempted_deposit_count") == null ? 0.0 : Double.parseDouble(String.valueOf(params.get("life_time_attempted_deposit_count"))));
			
			return CompletableFuture.completedFuture(userDetails);
		} catch (EmptyResultDataAccessException ex) {
			log.error("error while fetching details for suspicious user, user {} does not exist", userId);
			return CompletableFuture.completedFuture(new SuspiciousUserDetails(Result.STATS_NOT_FOUND));
		} catch (Exception ex) {
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public void addSuspiciousUser(UUID userId, boolean isPnlRuleViolated, boolean isGamePlayPnlRuleViolated,
			boolean isManualWithdrawalInLastHours, boolean isDepositFailureRuleViolated) {
		DataSource dataSource = jdbcTemplate.getJdbcWriteTemplate().getDataSource();
		
		if(dataSource == null)
			log.error("data source is null while adding suspicious user with userId: {}", userId);
		
		try(Connection connection = dataSource.getConnection();
				CallableStatement procedureQuery = connection.prepareCall(Queries.ADD_SUSPICIOUS_USER)) {
			
			procedureQuery.setObject(1, userId);
			procedureQuery.setBoolean(2, isPnlRuleViolated);
			procedureQuery.setBoolean(3, isGamePlayPnlRuleViolated);
			procedureQuery.setBoolean(4, isManualWithdrawalInLastHours);
			procedureQuery.setBoolean(5, isDepositFailureRuleViolated);
			
			procedureQuery.execute();	
		} catch (SQLException ex) {
			log.error("error while adding suspicious user with userId: {}", userId, ex);
		}
	}
	
	@Override
	public CompletableFuture<Response> markSuspiciousUser(MarkSuspiciousUserRequest request) {
		try {
			int updated = 0;
			
			if(request.getAction().equals(SuspiciousUserAction.MARK_FRAUD))
				updated = jdbcTemplate.getJdbcWriteTemplate().update(Queries.SET_SUSPICIOUS_USER_FRAUD, true, LocalDateTime.now(), request.getReason(), LocalDateTime.now(), request.getUserId());
			else
				updated = jdbcTemplate.getJdbcWriteTemplate().update(Queries.SET_SUSPICIOUS_USER_NOT_FRAUD, false, false, LocalDateTime.now(), LocalDateTime.now(), request.getUserId());
			
			if(updated == 1)
				return CompletableFuture.completedFuture(new Response(Result.SUCCESS));
			
			return CompletableFuture.completedFuture(new Response(Result.DB_ERROR));
		} catch (EmptyResultDataAccessException ex) {
			log.error("error while marking user suspicious, user {} does not exist", request.getUserId());
			return CompletableFuture.completedFuture(new Response(Result.STATS_NOT_FOUND));
		} catch (Exception ex) {
			log.error("error while performing action: {} for userId: {} ", request.getAction(), request.getUserId(), ex);
			return CompletableFuture.failedFuture(ex);
		}
	}

	@Override
	public CompletableFuture<UserDetailsResponse> getUserDetails(UUID userId) {
		DataSource dataSource = jdbcTemplate.getJdbcWriteTemplate().getDataSource();
		
		if(dataSource == null)
			return CompletableFuture.completedFuture(new UserDetailsResponse(Result.DB_ERROR));
		
		try(Connection connection = dataSource.getConnection();
				CallableStatement procedureQuery = connection.prepareCall(Queries.GET_USER_DETAILS)) {
			
			procedureQuery.setObject(1, userId);
			procedureQuery.setString(2, "");
			procedureQuery.execute();
				
			ResultSet resultSet = procedureQuery.getResultSet();
			String result = null;
				
			if(resultSet.next())
				result = resultSet.getString(1); //Sample result: 5_t or 0_f
			
			log.info("recieved result {}", result);
			if(result == null)
				return CompletableFuture.completedFuture(new UserDetailsResponse(Result.STATS_NOT_FOUND));
			
			UserDetailsResponse response = new UserDetailsResponse(Result.SUCCESS);
			String[] userDetails = result.split(Constant.UNDERSCORE);
			
			response.setLifetimeDepositAmount(Double.parseDouble(userDetails[0]));
			response.setIsUserSuspicious(userDetails[1].equals(Constant.TRUE));

			return CompletableFuture.completedFuture(response);
		} catch (SQLException ex) {
			log.error("error while fetching user details for userId: {}", userId, ex);
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public CompletableFuture<Response> markUserFraud(UUID userId) {
		DataSource dataSource = jdbcTemplate.getJdbcWriteTemplate().getDataSource();
		
		if (dataSource == null) {
			log.error("data source is null while while marking userId: {} as fraud", userId);
			return CompletableFuture.completedFuture(new Response(Result.DB_ERROR));
		}
		
		try (Connection connection = dataSource.getConnection();
				CallableStatement procedureQuery = connection.prepareCall(Queries.ADD_FRAUD_USER)) {
			
			procedureQuery.setObject(1, userId);
			procedureQuery.setString(2, Constant.FAILED);
			procedureQuery.execute();
			
			ResultSet resultSet = procedureQuery.getResultSet();
			String result = null;
			
			if (resultSet.next())
				result = resultSet.getString(1);
			
			if (result == null || Constant.FAILED.equals(result))
				return CompletableFuture.completedFuture(new Response(Result.DB_ERROR));
			
			return CompletableFuture.completedFuture(new Response(Result.SUCCESS));
		} catch (SQLException ex) {
			log.error("error occured while marking userId: {} as fraud", userId, ex);
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public CompletableFuture<BulkUserCashStatsResponse> getUserCashStatsInBulk(Set<UUID> userIds) {
		DataSource dataSource = jdbcTemplate.getJdbcWriteTemplate().getDataSource();
		
		if (dataSource == null)
			return CompletableFuture.completedFuture(new BulkUserCashStatsResponse(Result.DB_ERROR));
		
		try (Connection connection = dataSource.getConnection();
				CallableStatement procedureQuery = connection.prepareCall(Queries.FETCH_USER_CASH_STATS_IN_BULK)) {
			
			procedureQuery.setArray(1, connection.createArrayOf("UUID", userIds.stream().toArray(UUID[]::new)));
			procedureQuery.setArray(2, null);
			procedureQuery.execute();
				
			ResultSet resultSet = procedureQuery.getResultSet();
			Array result = null;
				
			if (resultSet.next())
				result = resultSet.getArray(1);
			
			/*
			 * result: userId_firstGamePlayedName_gamesTried_lifeTimeDepositAmount_lifeTimeWithdrawlAmount_lifeTimeRake_lifeTimeGamesPlayed
			 */
			if (result == null)
				return CompletableFuture.completedFuture(new BulkUserCashStatsResponse(Result.DB_ERROR));

			String[] usersCashStats = (String[]) result.getArray();
			Map<UUID, UserCashStats> userCashStatsMapping = new HashMap<>();
			
			for (int i = 0; i < usersCashStats.length; i++) {
				String[] userCashStats = Constant.UNDERSCORE_PATTERN.split(usersCashStats[i]);
				
				UserCashStats stats = new UserCashStats(userCashStats[1], userCashStats[2].isEmpty() ? null : userCashStats[2],
						Double.parseDouble(userCashStats[3]), Double.parseDouble(userCashStats[4]),
						Double.parseDouble(userCashStats[5]), Long.parseLong(userCashStats[6]));
				
				userCashStatsMapping.put(UUID.fromString(userCashStats[0]), stats);
			}
			
			return CompletableFuture.completedFuture(new BulkUserCashStatsResponse(Result.SUCCESS, userCashStatsMapping));
		} catch (SQLException ex) {
			log.error("error occured while fetching user cash stats in bulk", ex);
			return CompletableFuture.failedFuture(ex);
		}
	}

	@Override
	public void updateUserDepositInitiatedInfo(DepositInitiatedEvent depositInitiatedEvent) {
		try {
			int updated = jdbcTemplate.getJdbcWriteTemplate().update(Queries.UPDATE_DEPOSIT_INITIATED_INFO, LocalDateTime.now(), depositInitiatedEvent.getUserId());
			if(updated == 0)
				log.error("failed to update deposit-initiated for UID {}", depositInitiatedEvent.getUserId());
		} catch(Exception ex) {
			log.error("Error while updating deposit-initiated for UID {}", depositInitiatedEvent.getUserId(), ex);
		}
		
	}
	
	@Override
	public CompletableFuture<Double> fetchLifeTimeRummyRake(UUID userId) {
		try {
			Map<String, Object> result = jdbcTemplate.getJdbcReadTemplate().queryForMap(Queries.FETCH_LIFE_TIME_RUMMY_RAKE, userId);
			return CompletableFuture.completedFuture(Double.parseDouble(result.get("life_time_rummy_rake").toString()));
		} catch (Exception ex) {
			log.error("error occured while fetching life time rummy rake for userId: {}", userId, ex);
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public CompletableFuture<Response> updateDailyLoginStreak(UUID userId, int dailyLoginStreak) {
		try {
			int updatedRows = jdbcTemplate.getJdbcWriteTemplate().update(Queries.UPDATE_DAILY_LOGIN_STREAK, dailyLoginStreak, userId);
			
			if (updatedRows == 0)
				return CompletableFuture.completedFuture(new Response(Result.DB_ERROR));
				
			return CompletableFuture.completedFuture(new Response(Result.SUCCESS));
		} catch (Exception ex) {
			log.error("error occured while updating daily login streak: {} for userId: {}", dailyLoginStreak, userId, ex);
			return CompletableFuture.failedFuture(ex);
		}
	}
	
	@Override
	public CompletableFuture<Integer> fetchUserLoginStreak(UUID userId) {
		try {
			Map<String, Object> result = jdbcTemplate.getJdbcReadTemplate().queryForMap(Queries.FETCH_USER_LOGIN_STREAK, userId);
			return CompletableFuture.completedFuture(result.get("daily_login_streak") != null ? Integer.parseInt(result.get("daily_login_streak").toString()) : 0);
		} catch (Exception ex) {
			log.error("error occured while fetching life login streak for userId: {}", userId, ex);
			return CompletableFuture.failedFuture(ex);
		}
	}

}