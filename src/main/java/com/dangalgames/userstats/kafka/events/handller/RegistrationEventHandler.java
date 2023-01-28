package com.dangalgames.userstats.kafka.events.handller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.model.RegistrationEvent;
import com.dangalgames.userstats.service.UserStatsService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegistrationEventHandler {

	private Gson gson = new Gson();

	@Autowired
	UserStatsService userStatsService;

	@KafkaListener(topics = "#{'${pubsub.registration.topic}'}", groupId = "#{'${pubsub.registration.group-id}'}", containerFactory = "registrationKafkaListenerContainerFactory")
	public void handle(String message) {
		log.info("processing signup {}", message);
		RegistrationEvent event = gson.fromJson(message, RegistrationEvent.class);
		userStatsService.storeNewUser(event);

	}
	
}