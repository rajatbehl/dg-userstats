package com.dangalgames.userstats.service;

import java.util.Set;
import java.util.UUID;

import com.dangalgames.userstats.events.AppLaunchEvent;
import com.dangalgames.userstats.events.UpdateBalanceEvent;
import com.dangalgames.userstats.model.DepositEvent;
import com.dangalgames.userstats.model.DepositInitiatedEvent;
import com.dangalgames.userstats.model.RegistrationEvent;
import com.dangalgames.userstats.model.UserGameCashStatsEvent;
import com.dangalgames.userstats.model.UserStats;
import com.dangalgames.userstats.model.WithdrawalEvent;
import com.dangalgames.userstats.model.request.MarkSuspiciousUserRequest;
import com.dangalgames.userstats.model.response.BulkUserCashStatsResponse;
import com.dangalgames.userstats.model.response.DepositDetailsResponse;
import com.dangalgames.userstats.model.response.Response;
import com.dangalgames.userstats.model.response.SupportContactDetail;
import com.dangalgames.userstats.model.response.UserDetailsResponse;
import com.dangalgames.userstats.model.response.UserLoginStreakResponse;
import com.dangalgames.userstats.model.response.WithdrawDetailsResponse;

import reactor.core.publisher.Mono;

public interface UserStatsService {

	public void storeNewUser (RegistrationEvent registrationEvent);

	public void storeUserDeposit(DepositEvent depositEvent);

	public void storeGameCashStats(UserGameCashStatsEvent userGameCashStatsEvent);
	
	public void storeWithdrawals(WithdrawalEvent withdrawalEvent);

	public Mono<UserStats> userGameCount(String userId);
	
	public Mono<DepositDetailsResponse> getDepositDetails(UUID userId);
	
	public Mono<WithdrawDetailsResponse> getWithdrawlDetails(UUID userId);
	
	public void processAppLaunchEvent(AppLaunchEvent event);
	
	public void updateUserBalance(UpdateBalanceEvent event);

	public Mono<Response> markSuspiciousUser(MarkSuspiciousUserRequest request);

	public Mono<UserDetailsResponse> getUserDetails(UUID userId);
	
	public Mono<Response> markUserFraud(UUID userId);

	public void updateUserDepositInitiatedInfo(DepositInitiatedEvent depositInitiatedEvent);
	
	public Mono<SupportContactDetail> fetchSupportContactDetails(UUID userId);

	public Mono<BulkUserCashStatsResponse> getUserCashStatsInBulk(Set<UUID> userIds);
	
	public Mono<UserLoginStreakResponse> fetchUserLoginStreak(UUID userId);
	
}
