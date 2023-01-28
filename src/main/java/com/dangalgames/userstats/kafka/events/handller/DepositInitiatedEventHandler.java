package com.dangalgames.userstats.kafka.events.handller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.model.DepositInitiatedEvent;
import com.dangalgames.userstats.service.UserStatsService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DepositInitiatedEventHandler {
	
	@Autowired
	UserStatsService userStatsService;

	private Gson gson = new Gson();

	@KafkaListener(topics = "#{'${pubsub.deposits-initiated.topic}'}", groupId = "#{'${pubsub.deposits-initiated.group-id}'}", containerFactory = "depositInitiatedKafkaListenerContainerFactory")
	public void handle(String message) {
		DepositInitiatedEvent event = gson.fromJson(message, DepositInitiatedEvent.class);
		log.info("processing deposit initiated event {}", event);
		userStatsService.updateUserDepositInitiatedInfo(event);
	}
}
