package com.dangalgames.userstats.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.config.Config;
import com.dangalgames.userstats.config.ConsulConfig;
import com.dangalgames.userstats.dao.UserStatsDao;
import com.dangalgames.userstats.enums.Game;
import com.dangalgames.userstats.enums.Result;
import com.dangalgames.userstats.events.AppLaunchEvent;
import com.dangalgames.userstats.events.RetentionEvent;
import com.dangalgames.userstats.events.RetentionEvent.Event;
import com.dangalgames.userstats.events.UpdateBalanceEvent;
import com.dangalgames.userstats.model.CreditOperation;
import com.dangalgames.userstats.model.DepositEvent;
import com.dangalgames.userstats.model.DepositInitiatedEvent;
import com.dangalgames.userstats.model.RegistrationEvent;
import com.dangalgames.userstats.model.SuspiciousUserDetails;
import com.dangalgames.userstats.model.UserGameCashStats;
import com.dangalgames.userstats.model.UserGameCashStatsEvent;
import com.dangalgames.userstats.model.UserStats;
import com.dangalgames.userstats.model.WithdrawalEvent;
import com.dangalgames.userstats.model.request.CreditCurrencyRequest;
import com.dangalgames.userstats.model.request.CreditCurrencyRequest.Operation;
import com.dangalgames.userstats.model.request.MarkSuspiciousUserRequest;
import com.dangalgames.userstats.model.response.BulkUserCashStatsResponse;
import com.dangalgames.userstats.model.response.DepositDetailsResponse;
import com.dangalgames.userstats.model.response.Response;
import com.dangalgames.userstats.model.response.SupportContactDetail;
import com.dangalgames.userstats.model.response.UserDetailsResponse;
import com.dangalgames.userstats.model.response.UserLoginStreakResponse;
import com.dangalgames.userstats.model.response.WithdrawDetailsResponse;
import com.dangalgames.userstats.utils.Constant;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserStatsServiceImpl implements UserStatsService {
	
	@Autowired
	private UserStatsDao userStatsDao;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Autowired
	private Config config;
	
	@Autowired
	private ConsulConfig consulConfig;
	
	private static final String LATEST_EVENT = "latest";
	private static final String WON = "won";
	private static final String LOST = "lost";
	private static final Pattern PIPE_SPLITTER = Pattern.compile("\\|");
	private static final Pattern COMMA_SPLITTER = Pattern.compile(",");
	private static final String FAILED = "Failed";
	private static final String COMMA = ",";
	private static final String USER_NOT_EXIST = "USER_NOT_EXIST";
	private static final String GAME_NAME = "gameName";
	private static final String TRANSACTION_TYPE = "transactionType";
	private static final String WINNINGS = "Winnings";
	private static final String QUALIFYING_RUMMY_RAKE_KEY = "support.qualifying-rummy-rake";
	private static final String SUPPORT_CONTACT_NUMBER_KEY = "support.number";
	
	@Override
	public void storeNewUser(RegistrationEvent registrationEvent) {
		log.info("Calling storeNewUser dao methods for {}", registrationEvent);
		userStatsDao.storeNewUser(registrationEvent);
	}

	@Override
	public void storeUserDeposit(DepositEvent depositEvent) {
		log.info("Calling storeUserDeposit dao methods for {}", depositEvent);
		Mono.fromFuture(userStatsDao.storeUserDepositInfo(depositEvent)).subscribe(isFirstDeposit -> {
			if(isFirstDeposit)
				eventPublisher.publishEvent(depositEvent);
			publishRetentionEvent(depositEvent);
		});
	}

	@Override
	public void storeGameCashStats(UserGameCashStatsEvent userGameCashStatsEvent) {
		log.info("Calling storeGameCashStats dao methods for {}", userGameCashStatsEvent);
		publishRetentionEvent(userGameCashStatsEvent);
		Mono.fromFuture(userStatsDao.storeAndGetGameCashStats(userGameCashStatsEvent)).subscribe(result -> {
			log.info("received result from storeGameCashStats sp {}", result);
		  	if(FAILED.equals(result) || Result.DB_ERROR.name().equals(result)) {
		  		log.error("Failed to store cash game stats {} received result {}", userGameCashStatsEvent, result);
		  		return;
		  	}
		  	
			/**
			 * Stored Procedure Output
			 * isFirstGamePlayed | gamesTried | lifeTimeGamesWon | lifeTimeGamesLost | lifeTimeWithdrawalAmount | lifeTimeDepositAmount | withdrawalBalance | depositBalance | lifeTimeRummyRake | lifeTimeRake | lastGamePlayedResult | lifeTimeRummyCashTourneyWinnings | lifeTimePokerCashTourneyWinnings
			 */
		  	String[] resultArray = PIPE_SPLITTER.split(result);
			if(Boolean.parseBoolean(resultArray[0]))
				eventPublisher.publishEvent(userGameCashStatsEvent);
			
			updateGamesTried(UUID.fromString(userGameCashStatsEvent.getUserId()), resultArray[1], userGameCashStatsEvent.getGame());
			
			UserGameCashStats userGameCashStats = new UserGameCashStats();
			userGameCashStats.setUserId(UUID.fromString(userGameCashStatsEvent.getUserId()));
			userGameCashStats.setLifeTimeCashGamesWon(Integer.parseInt(resultArray[2]));
			userGameCashStats.setLifeTimeCashGamesLost(Integer.parseInt(resultArray[3]));
			userGameCashStats.setLifeTimeWithdrawalAmount(Double.parseDouble(resultArray[4]));
			userGameCashStats.setLifeTimeDepositAmount(Double.parseDouble(resultArray[5]));
			userGameCashStats.setWithdrawalBalance(Double.parseDouble(resultArray[6]));
			userGameCashStats.setDepositBalance(Double.parseDouble(resultArray[7]));
			userGameCashStats.setLifeTimeRummyRake(Double.parseDouble(resultArray[8]));
			userGameCashStats.setLifeTimeRake(Double.parseDouble(resultArray[9]));
			userGameCashStats.setLastGamePlayedResult(resultArray[10]);
			userGameCashStats.setLifeTimeRummyCashTourneyWinnings(Double.parseDouble(resultArray[11]));
			userGameCashStats.setLifeTimePokerCashTourneyWinnings(Double.parseDouble(resultArray[12]));
			
			publishRetentionEvent(userGameCashStats);
		});
	}

	@Override
	public void storeWithdrawals(WithdrawalEvent withdrawalEvent) {
		log.info("Calling storeGameCashStats dao methods for {}", withdrawalEvent);
		userStatsDao.storeWithdrawals(withdrawalEvent);

	}

	@Override
	public Mono<UserStats> userGameCount(String userId) {
		return Mono.fromFuture(userStatsDao.getUserGameCount(userId));
	}
	
	@Override
	public Mono<DepositDetailsResponse> getDepositDetails(UUID userId) {
		log.info("getting deposit details for UID {}", userId);
		return Mono.fromFuture(userStatsDao.getDepositDetails(userId))
				.onErrorReturn(new DepositDetailsResponse(Result.DB_ERROR));
	}
	
	@Override
	public Mono<WithdrawDetailsResponse> getWithdrawlDetails(UUID userId) {
		log.info("getting withdrawal details for UID {}", userId);
		return Mono.fromFuture(userStatsDao.getWithdrawalDetails(userId))
				.onErrorReturn(new WithdrawDetailsResponse(Result.DB_ERROR));
	}
	
	@Override
	public void processAppLaunchEvent(AppLaunchEvent event) {
		log.info("processing app launch event for UID {}", event.getUserId());
		Mono.fromFuture(userStatsDao.saveLastSeenAndGetUserStats(event.getUserId()))
		  .subscribe(result -> {
			  	log.info("received result from saveLastSeenAndGetUserStats sp {}", result);
			  	
			  	if (USER_NOT_EXIST.equals(result)) {
			  		log.info("Not able to send app launch event for UID {} as user does not exist", event.getUserId());
			  		return;
			  	} else if (FAILED.equals(result) || Result.DB_ERROR.name().equals(result)) {
			  		log.error("Failed to save last seen for UID {} received result {}", event.getUserId(), result);
			  		return;
			  	}
				
			  	/**
				 * Stored Procedure Output
				 * LastGamePlayed | LastGameResult | LastGamePlayedTime | LastDeposit | LastDepositTime | LastSeen | DailyLoginStreak
				 */
				String[] resultArray = PIPE_SPLITTER.split(result);

				RetentionEvent retentionEvent = new RetentionEvent(event.getUserId(), LATEST_EVENT);
				retentionEvent.setLastGamePlayed(resultArray[0]);
				retentionEvent.setLastGameResult(resultArray[1]);
				retentionEvent.setLastGamePlayedTime(resultArray[2]);
				retentionEvent.setLastDepositAmount(resultArray[3]);
				retentionEvent.setLastDepositTime(resultArray[4]);
				retentionEvent.setLastSeen(resultArray[5]);
				retentionEvent.setTimestamp(event.getTimestamp());
				eventPublisher.publishEvent(retentionEvent);
				
				int userDailyLoginStreak = Integer.parseInt(resultArray[6]);
				int applicableDailyLoginStreak = consulConfig.getInt(Constant.LOGIN_STREAK_DAYS_KEY, config.getLoginStreakDays());
				
				if (userDailyLoginStreak >= applicableDailyLoginStreak)
					creditLoginIncentive(event.getUserId())
					.subscribe(response -> {
						if (Result.SUCCESS == response.getResult())
							Mono.fromFuture(userStatsDao.updateDailyLoginStreak(event.getUserId(), userDailyLoginStreak - applicableDailyLoginStreak)).subscribe();
					});
		  });
	}
	
	@Override
	public void updateUserBalance(UpdateBalanceEvent event) {
		log.info("processing update balance event for {}", event);
		userStatsDao.updateUserBalance(event);
		validateIfUserIsSuspicious(event);
	}
	
	
	@Override
	public Mono<Response> markSuspiciousUser(MarkSuspiciousUserRequest request) {
		log.info("received request to mark suspicious user fraud/not-fraud {}", request);
		return Mono.fromFuture(userStatsDao.markSuspiciousUser(request));
	}

	@Override
	public Mono<UserDetailsResponse> getUserDetails(UUID userId) {
		return Mono.fromFuture(userStatsDao.getUserDetails(userId));
	}
	
	@Override
	public Mono<Response> markUserFraud(UUID userId) {
		log.info("received request to mark userId: {} as fraud", userId);
		return Mono.fromFuture(userStatsDao.markUserFraud(userId));
	}
	
	@Override
	public Mono<SupportContactDetail> fetchSupportContactDetails(UUID userId) {
		log.info("received request to fetch support contact details for userId: {}", userId);
		return Mono.fromFuture(userStatsDao.fetchLifeTimeRummyRake(userId))
				.flatMap(lifeTimeRummyRake -> {
					if (lifeTimeRummyRake < consulConfig.getDouble(QUALIFYING_RUMMY_RAKE_KEY, config.getQualifyingRummyRake()))
						return Mono.just(new SupportContactDetail(Result.SUPPORT_CONTACT_NUMBER_NOT_AVAILABLE));
					
					return Mono.just(new SupportContactDetail(Result.SUCCESS, consulConfig.getString(SUPPORT_CONTACT_NUMBER_KEY, config.getSupportContactNumber())));
				})
				.doOnError(error -> log.error("error occured while fetching contact support details for userId: {}", userId))
				.onErrorReturn(new SupportContactDetail(Result.DB_ERROR));
	}

	@Override
	public Mono<BulkUserCashStatsResponse> getUserCashStatsInBulk(Set<UUID> userIds) {
		log.info("received request to fetch user cash stats in bulk");
		return Mono.fromFuture(userStatsDao.getUserCashStatsInBulk(userIds))
				.onErrorReturn(new BulkUserCashStatsResponse(Result.DB_ERROR));
	}
	
	@Override
	public Mono<UserLoginStreakResponse> fetchUserLoginStreak(UUID userId) {
		log.info("received request to fetch login streak for userId: {}", userId);
		
		return Mono.fromFuture(userStatsDao.fetchUserLoginStreak(userId))
				.flatMap(userLoginStreak -> {
					int applicableDailyLoginStreak = consulConfig.getInt(Constant.LOGIN_STREAK_DAYS_KEY, config.getLoginStreakDays());
					double totalIncentiveAmount = consulConfig.getDouble(Constant.LOGIN_STREAK_DEPOSIT_INCENTIVE_KEY, config.getLoginStreakDepositIncentive()) 
							+ consulConfig.getDouble(Constant.LOGIN_STREAK_WITHDRAWAL_INCENTIVE_KEY, config.getLoginStreakWithdrawalIncentive()) 
							+ consulConfig.getDouble(Constant.LOGIN_STREAK_PROMO_INCENTIVE_KEY, config.getLoginStreakPromoIncentive()) 
							+ consulConfig.getDouble(Constant.LOGIN_STREAK_LOCKED_INCENTIVE_KEY, config.getLoginStreakLockedIncentive());
					double maxIncentiveAmount = consulConfig.getDouble(Constant.LOGIN_STREAK_MAX_INCENTIVE_KEY, config.getLoginStreakMaxIncentive());
					
					return Mono.just(new UserLoginStreakResponse(Result.SUCCESS, userLoginStreak, applicableDailyLoginStreak, totalIncentiveAmount, maxIncentiveAmount));
				})
				.onErrorReturn(new UserLoginStreakResponse(Result.DB_ERROR));
	}

	private void publishRetentionEvent(UserGameCashStatsEvent event) {
		RetentionEvent retentionEvent = new RetentionEvent(UUID.fromString(event.getUserId()), LATEST_EVENT);
		
		retentionEvent.setLastGamePlayed(event.getGame());
		retentionEvent.setLastGamePlayedTime(LocalDateTime.now().toString());
		retentionEvent.setLastGameResult(event.getWinnings() > 0 ? WON : LOST);
		retentionEvent.setTimestamp(event.getCurrentTime()/1000);
		
		Mono.fromFuture(userStatsDao.getLastDepositDetails(UUID.fromString(event.getUserId())))
			.doOnError(error -> eventPublisher.publishEvent(retentionEvent))
			.subscribe(details -> {
				retentionEvent.setLastDepositAmount(details.getLastDepositAmount());
				retentionEvent.setLastDepositTime(details.getLastDepositTime());
				
				eventPublisher.publishEvent(retentionEvent);
			});
	}
	
	private void publishRetentionEvent(UserGameCashStats userGameCashStats) {
		log.info("received userGameCashStats {}", userGameCashStats);
		RetentionEvent retentionEvent = new RetentionEvent(userGameCashStats.getUserId());
		double winPercentage = 0;
		if(userGameCashStats.getLifeTimeCashGamesWon() != 0 || userGameCashStats.getLifeTimeCashGamesLost() != 0)
			winPercentage = ((double) userGameCashStats.getLifeTimeCashGamesWon() / (userGameCashStats.getLifeTimeCashGamesWon() + userGameCashStats.getLifeTimeCashGamesLost())) * 100.0;
		double pnl = userGameCashStats.getLifeTimeWithdrawalAmount() + userGameCashStats.getWithdrawalBalance() + userGameCashStats.getDepositBalance() - userGameCashStats.getLifeTimeDepositAmount();
		double rakeToDepositPercentage = 0;
		if(userGameCashStats.getLifeTimeDepositAmount() != 0)
			rakeToDepositPercentage = (userGameCashStats.getLifeTimeRake() / userGameCashStats.getLifeTimeDepositAmount()) * 100.0;
		
		Map<String, Object> userProperties = new HashMap<>();
		userProperties.put(Constant.CASH_GAME_WIN_PERCENTAGE, Math.floor(winPercentage * 100.0) / 100.0);
		userProperties.put(Constant.PNL, pnl);
		userProperties.put(Constant.TOTAL_RUMMY_RAKE, userGameCashStats.getLifeTimeRummyRake());
		userProperties.put(Constant.TOTAL_RAKE, userGameCashStats.getLifeTimeRake());
		userProperties.put(Constant.RAKE_TO_DEPOSIT_PERCENTAGE, Math.floor(rakeToDepositPercentage * 100.0) / 100.0);
		userProperties.put(Constant.TOTAL_RUMMY_CASH_TOURNEY_WINNINGS, userGameCashStats.getLifeTimeRummyCashTourneyWinnings());
		userProperties.put(Constant.TOTAL_POKER_CASH_TOURNEY_WINNINGS, userGameCashStats.getLifeTimePokerCashTourneyWinnings());
		userProperties.put(Constant.TOTAL_CASH_TOURNEY_WINNINGS, userGameCashStats.getLifeTimeRummyCashTourneyWinnings() + userGameCashStats.getLifeTimePokerCashTourneyWinnings());
		
		retentionEvent.setUserProperties(userProperties);
		retentionEvent.setEvents(Collections.singletonList(new Event(Constant.LAST_CASH_GAME_RESULT,
				Collections.singletonMap(Constant.GAME_RESULT, userGameCashStats.getLastGamePlayedResult()))));
		
		eventPublisher.publishEvent(retentionEvent);
	}
	
	private void publishRetentionEvent(DepositEvent depositEvent) {
		RetentionEvent retentionEvent = new RetentionEvent(UUID.fromString(depositEvent.getUserId()), LATEST_EVENT);
		
		retentionEvent.setLastDepositAmount(String.valueOf(depositEvent.getAmount()));
		retentionEvent.setLastDepositTime(Instant.ofEpochMilli(depositEvent.getDepositFulfillDate()).toString());
		retentionEvent.setTimestamp(depositEvent.getTimestamp());
		
		Mono.fromFuture(userStatsDao.getLastGameDetails(UUID.fromString(depositEvent.getUserId())))
			.doOnError(error -> eventPublisher.publishEvent(retentionEvent))
			.subscribe(details -> {
				retentionEvent.setLastGamePlayed(details.getLastGamePlayed());
				retentionEvent.setLastGamePlayedTime(details.getLastGamePlayedTime());
				retentionEvent.setLastGameResult(details.getLastGameResult());
				
				eventPublisher.publishEvent(retentionEvent);
			});
	}
	
	private void updateGamesTried(UUID userId, String gamesTried, String gamePlayed) {
		if(Strings.isNullOrEmpty(gamesTried)) {
			gamesTried = Game.getIdByName(gamePlayed);
			userStatsDao.updateGamesTried(userId, gamesTried);
		}else {
			String[] gamesTriedIds = COMMA_SPLITTER.split(gamesTried);
			String gamePlayedId = Game.getIdByName(gamePlayed);
			if(gamePlayedId == null) {
				log.error("###IMPORTANT### Need to update mapping for game {}", gamePlayed);
				return;
			}
			boolean isNewGameTried = true;
			for(String id : gamesTriedIds) {
				if(id.equals(gamePlayedId)) {
					isNewGameTried = false;
					break;
				}
			}
			
			if(isNewGameTried) {
				gamesTried = gamesTried + COMMA + gamePlayedId;
				userStatsDao.updateGamesTried(userId, gamesTried);
			}
		}
	}
	
	private void validateIfUserIsSuspicious(UpdateBalanceEvent event) {
		if(event.getMetaData() !=  null && event.getMetaData().get(GAME_NAME) != null && WINNINGS.equals(event.getMetaData().get(TRANSACTION_TYPE))) {
			Mono.fromFuture(userStatsDao.getUserDetailsToCheckIfSuspicious(event.getUserId()))
			.doOnError(error -> log.error("Error while fetching user details for gameEvent {}", event, error)).subscribe(details -> {
				if(details.getResult() == Result.SUCCESS)
					checkViolation(details, event);
			});
		}
		
	}
	
	private void checkViolation(SuspiciousUserDetails details, UpdateBalanceEvent event) {
		boolean isPnlRuleViolated = (details.getTotalWithdrawalAmount() + event.getDepositBalance()
				+ event.getWithdrawalBalance() + event.getPokerBalance() - details.getTotalDepositAmount())
				/ (details.getTotalDepositAmount() + 1) > consulConfig.getInt("suspicious-user.threshold.pnl",
						config.getPnlThreshold());

		boolean isGamePlayPnlRuleViolated = (details.getTotalWithdrawalAmount() + event.getDepositBalance()
				+ event.getWithdrawalBalance() + event.getPokerBalance() - details.getTotalDepositAmount())
				/ (details.getTotalRake() + 1) > consulConfig.getInt("suspicious-user.threshold.game-play-pnl",
						config.getGamePlayPnlThreshold());
				
		boolean isApprovedWithdrawalInLastHours =  details.getLastManualWithdrawalTime() !=  null 
	    	    		&& Duration.between(details.getLastManualWithdrawalTime().toLocalDateTime(), LocalDateTime.now()).toHours() 
	    	    		<= consulConfig.getInt("suspicious-user.threshold.last-manual-withdrawal", config.getLastManualWithdrawalThreshold()) ;
		
		boolean isDepositFailureRuleViolated = false;
		
		log.info("Lifetime deposit count {}", details.getLifetimeDepositCount());
		
		if (details.getLifetimeDepositCount() > 0) {
			isDepositFailureRuleViolated = (details.getLifetimeDepositCount()
					/ (details.getLifetimeAttemptedDepositCount() + 1)) < consulConfig
							.getDouble("deposit-failure-threshold", config.getDepositFailureThreshold());
		}
	    	
		log.info("suspicious user violation result with details {} and userBalanaceEvent {}, isPnlRuleViolated {}, "
						+ "isGamePlayPnlRuleViolated {}, isApprovedWithdrawalInLastHours {} , isDepositFailure {}",
				details, event, isPnlRuleViolated, isGamePlayPnlRuleViolated, isApprovedWithdrawalInLastHours,
				isDepositFailureRuleViolated);

		if((isPnlRuleViolated || isGamePlayPnlRuleViolated || isDepositFailureRuleViolated) && !isApprovedWithdrawalInLastHours) {
	    	log.info("marking user {} suspicious", event.getUserId());
	    	userStatsDao.addSuspiciousUser(event.getUserId(), isPnlRuleViolated, isGamePlayPnlRuleViolated, isApprovedWithdrawalInLastHours, isDepositFailureRuleViolated);
	    }
	}

	@Override
	public void updateUserDepositInitiatedInfo(DepositInitiatedEvent depositInitiatedEvent) {
		log.info("Calling storeUserDeposit dao methods for {}", depositInitiatedEvent);
		userStatsDao.updateUserDepositInitiatedInfo(depositInitiatedEvent);
	}
	
	private Mono<Response> creditLoginIncentive(UUID userId) {
		CreditOperation creditOperation = fetchLoginIncentiveDetails();
		List<Operation> operations = creditOperation.getOperations();
		
		if (operations.isEmpty()) {
			log.info("empty wallet operations while crediting login incentive for userId: {}", userId);
			return Mono.just(new Response(Result.CREDIT_CURRENCY_FAILED));
		}
		
		CreditCurrencyRequest request = new CreditCurrencyRequest();
		request.setUserId(userId);
		request.setTransactionType(Constant.LOGIN_INCENTIVE);
		request.setOperations(operations);
		request.setDescription(String.format(Constant.LOGIN_INCENTIVE_DESCRIPTION, creditOperation.getTotalAmountToCredit()));
		
		return walletService.creditCurrency(request)
				.flatMap(response -> {
					if (Result.SUCCESS != response.getResult())
						log.error("failed to credit currency for request: {} as result received: {} ", request, response.getResult());
					
					return Mono.just(new Response(response.getResult()));
				});
	}
	
	private CreditOperation fetchLoginIncentiveDetails() {
		List<Operation> operations = new ArrayList<>();
		
		double depositIncentive = consulConfig.getDouble(Constant.LOGIN_STREAK_DEPOSIT_INCENTIVE_KEY, config.getLoginStreakDepositIncentive());
		double withdrawalIncentive = consulConfig.getDouble(Constant.LOGIN_STREAK_WITHDRAWAL_INCENTIVE_KEY, config.getLoginStreakWithdrawalIncentive());
		double promoIncentive = consulConfig.getDouble(Constant.LOGIN_STREAK_PROMO_INCENTIVE_KEY, config.getLoginStreakPromoIncentive());
		double lockedIncentive = consulConfig.getDouble(Constant.LOGIN_STREAK_LOCKED_INCENTIVE_KEY, config.getLoginStreakLockedIncentive());
		
		if (depositIncentive > 0)
			operations.add(new Operation(Constant.DEPOSIT, depositIncentive));
		
		if (withdrawalIncentive > 0)
			operations.add(new Operation(Constant.WITHDRAWAL, withdrawalIncentive));
		
		if (promoIncentive > 0)
			operations.add(new Operation(Constant.PROMO_BONUS, promoIncentive));
		
		if (lockedIncentive > 0)
			operations.add(new Operation(Constant.LOCKED_BONUS, lockedIncentive));
		
		return new CreditOperation(operations, depositIncentive + withdrawalIncentive + promoIncentive + lockedIncentive);
	}
	
}