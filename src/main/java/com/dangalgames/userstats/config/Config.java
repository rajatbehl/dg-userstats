package com.dangalgames.userstats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class Config {

	@Value("${pubsub.namespace}")
	private String namespace;

	@Value("${pubsub.registration.topic}")
	private String registrationTopic;

	@Value("${pubsub.registration.group-id}")
	private String registrationGroupId;

	@Value("${pubsub.registration.consumer-count}")
	private int registrationConsumerCount;

	@Value("${pubsub.deposits.topic}")
	private String depositsTopic;

	@Value("${pubsub.deposits.group-id}")
	private String depositsGroupId;

	@Value("${pubsub.deposits.consumer-count}")
	private int depositsConsumerCount;

	@Value("${pubsub.cashstats.topic}")
	private String cashstatsTopic;

	@Value("${pubsub.cashstats.group-id}")
	private String cashstatsGroupId;

	@Value("${pubsub.cashstats.consumer-count}")
	private int cashstatsCounsumerCount;

	@Value("${pubsub.withdrawal.topic}")
	private String withdrawalTopic;

	@Value("${pubsub.withdrawal.group-id}")
	private String withdrawalGroupId;

	@Value("${pubsub.withdrawal.consumer-count}")
	private int withdrawalCounsumerCount;
	
	@Value("${pubsub.first-deposit.topic}")
	private String firstDepositTopic;
	
	@Value("${pubsub.first-game.topic}")
	private String firstCashGameTopic;
	
	@Value("${pubsub.retention-event.topic}")
	private String retentionEventTopic;
	
	@Value("${pubsub.applaunch-event.topic}")
	private String appLaunchEventTopic;
	
	@Value("${pubsub.applaunch-event.group-id}")
	private String appLaucnhGroupId;

	@Value("${pubsub.applaunch-event.consumer-count}")
	private int appLaunchCounsumerCount;
	
	@Value("${pubsub.update-balance.topic}")
	private String updateBalanceTopic;
	
	@Value("${pubsub.update-balance.group-id}")
	private String updateBalanceGroupId;

	@Value("${pubsub.update-balance.consumer-count}")
	private int updateBalanceConsumerCount;

	@Value("${db-async-executor.pool-size.core}")
	private int dbAsyncCorePoolSize;

	@Value("${db-async-executor.pool-size.max}")
	private int dbAsyncMaxPoolSize;
	
	@Value("${event-async-executor.pool-size.core}")
	private int eventAsyncCorePoolSize;
	
	@Value("${event-async-executor.pool-size.max}")
	private int eventAsyncMaxPoolSize;
	
	@Value("${consul.url}")
	private String consulURL;
	
	@Value("${consul.path}")
	private String consulPath;
	
	@Value("${suspicious-user.threshold.pnl}")
	private int pnlThreshold;
	
	@Value("${suspicious-user.threshold.game-play-pnl}")
	private int gamePlayPnlThreshold;
	
	@Value("${suspicious-user.threshold.last-manual-withdrawal}")
	private int lastManualWithdrawalThreshold;
	
	@Value("${suspicious-user.threshold.depositFailure}")
	private double depositFailureThreshold;

	@Value("${pubsub.deposits-initiated.topic}")
	private String depositsInitiatedTopic;

	@Value("${pubsub.deposits-initiated.group-id}")
	private String depositsInitiatedGroupId;

	@Value("${pubsub.deposits-initiated.consumer-count}")
	private int depositsInitiatedConsumerCount;
	
	@Value("${support.qualifying-rummy-rake}")
	private double qualifyingRummyRake;
	
	@Value("${support.number}")
	private String supportContactNumber;
	
	@Value("${spring.aws.secretmanager.writeSecretName}")
	private String writeSecretName;
	
	@Value("${spring.aws.secretmanager.readSecretName}")
	private String readSecretName;
	
	@Value("${spring.aws.secretmanager.region}")
	private String region;
	
	@Value("${streak.login.days}")
	private int loginStreakDays;
	
	@Value("${streak.login.incentive.deposit}")
	private double loginStreakDepositIncentive;
	
	@Value("${streak.login.incentive.withdrawal}")
	private double loginStreakWithdrawalIncentive;
	
	@Value("${streak.login.incentive.promo-bonus}")
	private double loginStreakPromoIncentive;
	
	@Value("${streak.login.incentive.locked-bonus}")
	private double loginStreakLockedIncentive;
	
	@Value("${streak.login.incentive.max}")
	private double loginStreakMaxIncentive;
	
	// WebClient
	@Value("${webclient.maxConnections}")
	private int maxConnections;

	@Value("${webclient.maxIdleTime}")
	private long maxIdleTime;

	@Value("${webclient.maxLifeTime}")
	private long maxLifeTime;

	@Value("${webclient.pendingAcquireTimeout}")
	private long pendingAcquireTimeout;

	@Value("${webclient.evictInBackground}")
	private long evictInBackground;
	
	//Endpoints
	@Value("${endpoints.wallet.credit}")
	private String creditUserWalletUrl;

}
