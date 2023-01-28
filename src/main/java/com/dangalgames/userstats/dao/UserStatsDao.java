package com.dangalgames.userstats.dao;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
import com.dangalgames.userstats.model.response.DepositDetailsResponse;
import com.dangalgames.userstats.model.response.Response;
import com.dangalgames.userstats.model.response.UserDetailsResponse;
import com.dangalgames.userstats.model.response.WithdrawDetailsResponse;

public interface UserStatsDao {

	public void storeNewUser(RegistrationEvent registrationEvent);

	public CompletableFuture<Boolean> storeUserDepositInfo(DepositEvent depositEvent);

	public CompletableFuture<String> storeAndGetGameCashStats(UserGameCashStatsEvent userGameCashStatsEvent);
	
	public void storeWithdrawals(WithdrawalEvent withdrawalEvent);

	public CompletableFuture<UserStats> getUserGameCount(String userId);
	
	public CompletableFuture<DepositDetailsResponse> getDepositDetails(UUID userId);
	
	public CompletableFuture<WithdrawDetailsResponse> getWithdrawalDetails(UUID userId);
	
	public CompletableFuture<LastDepositDetails> getLastDepositDetails(UUID userId);
	
	public CompletableFuture<LastGameDetails> getLastGameDetails(UUID userId);
	
	public CompletableFuture<String> saveLastSeenAndGetUserStats(UUID userId);
	
	public void updateGamesTried(UUID userId, String gamesTried);
	
	public void updateUserBalance(UpdateBalanceEvent event);

	public CompletableFuture<SuspiciousUserDetails> getUserDetailsToCheckIfSuspicious(UUID userId);
	
	public void addSuspiciousUser(UUID userId, boolean isPnlRuleViolated, boolean isGamePlayPnlRuleViolated, boolean isManualWithdrawalInLastHours, boolean isDepositFailureRuleViolated);
	
	public CompletableFuture<Response> markSuspiciousUser(MarkSuspiciousUserRequest request);

	public CompletableFuture<UserDetailsResponse> getUserDetails(UUID userId);
	
	public CompletableFuture<Response> markUserFraud(UUID userId);

	public void updateUserDepositInitiatedInfo(DepositInitiatedEvent depositInitiatedEvent);
	
	public CompletableFuture<Double> fetchLifeTimeRummyRake(UUID userId);

	public CompletableFuture<BulkUserCashStatsResponse> getUserCashStatsInBulk(Set<UUID> userIds);
	
	public CompletableFuture<Response> updateDailyLoginStreak(UUID userId, int dailyLoginStreak);
	
	public CompletableFuture<Integer> fetchUserLoginStreak(UUID userId);
	
}