package com.dangalgames.userstats.kafka.events.handller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.model.DepositEvent;
import com.dangalgames.userstats.service.UserStatsService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DepositEventHandler {
	
	@Autowired
	UserStatsService userStatsService;

	private Gson gson = new Gson();

	@KafkaListener(topics = "#{'${pubsub.deposits.topic}'}", groupId = "#{'${pubsub.deposits.group-id}'}", containerFactory = "depositKafkaListenerContainerFactory")
	public void handle(String message) {
		DepositEvent event = gson.fromJson(message, DepositEvent.class);
		log.info("processing deposit event {}", event);
		userStatsService.storeUserDeposit(event);

	}
}
