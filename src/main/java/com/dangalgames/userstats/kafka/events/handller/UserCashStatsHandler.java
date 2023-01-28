package com.dangalgames.userstats.kafka.events.handller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.model.UserGameCashStatsEvent;
import com.dangalgames.userstats.service.UserStatsService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserCashStatsHandler {

	@Autowired
	UserStatsService userStatsService;

	private Gson gson = new Gson();

	@KafkaListener(topics = "#{'${pubsub.cashstats.topic}'}", groupId = "#{'${pubsub.cashstats.group-id}'}", containerFactory = "userGameStatsContainerFactory")
	public void handle(String message) {
		UserGameCashStatsEvent event = gson.fromJson(message, UserGameCashStatsEvent.class);
		log.info("processing cash stats event {}", event);
		userStatsService.storeGameCashStats(event);
	}

}
