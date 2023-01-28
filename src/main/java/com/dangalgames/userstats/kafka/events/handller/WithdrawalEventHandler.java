package com.dangalgames.userstats.kafka.events.handller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.model.WithdrawalEvent;
import com.dangalgames.userstats.service.UserStatsService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WithdrawalEventHandler {
	
	@Autowired
	UserStatsService userStatsService;
	
	private Gson gson = new Gson();
	
	@KafkaListener(topics = "#{'${pubsub.withdrawal.topic}'}", groupId = "#{'${pubsub.withdrawal.group-id}'}", containerFactory = "withdrawalKafkaListenerContainerFactory")
	public void handle(String message) {
		WithdrawalEvent event = gson.fromJson(message, WithdrawalEvent.class);
		log.info("processing withdrawal event {}", event);
		userStatsService.storeWithdrawals(event);
	}

}
