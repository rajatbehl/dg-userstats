package com.dangalgames.userstats.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.dangalgames.userstats.config.Config;
import com.dangalgames.userstats.config.ConsulConfig;
import com.dangalgames.userstats.events.RetentionEvent;
import com.dangalgames.userstats.model.DepositEvent;
import com.dangalgames.userstats.model.UserGameCashStatsEvent;
import com.dangalgames.userstats.publisher.KafkaPublisher;
import com.google.gson.Gson;

@Component
public class EventsListener {
	
	private Gson gson = new Gson();
	
	@Autowired
	private KafkaPublisher publisher;
	
	@Autowired
	private Config config;
	
	@Autowired
	private ConsulConfig consulConfig;
	
	@EventListener
	@Async("event_executor")
	void handleFirstDepositEvent(DepositEvent depositEvent) {
		publisher.sendMessage(gson.toJson(depositEvent), consulConfig.getString("pubsub.first-deposit.topic", config.getFirstDepositTopic()));
	}
	
	@EventListener
	@Async("event_executor")
	void handleFirstCashGameEvent(UserGameCashStatsEvent cashStatsEvent) {
		publisher.sendMessage(gson.toJson(cashStatsEvent), consulConfig.getString("pubsub.first-game.topic",config.getFirstCashGameTopic()));
	}
	
	@EventListener
	@Async("event_executor")
	void handleRetentionEvent(RetentionEvent retentionEvent) {
		publisher.sendMessage(gson.toJson(retentionEvent), consulConfig.getString("pubsub.retention-event.topic", config.getRetentionEventTopic()));
	}
}