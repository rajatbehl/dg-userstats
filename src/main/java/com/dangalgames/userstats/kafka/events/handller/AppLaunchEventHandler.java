package com.dangalgames.userstats.kafka.events.handller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.events.AppLaunchEvent;
import com.dangalgames.userstats.service.UserStatsService;
import com.google.gson.Gson;

@Service
public class AppLaunchEventHandler {
	
	@Autowired
	UserStatsService userStatsService;

	private Gson gson = new Gson();

	@KafkaListener(topics = "#{'${pubsub.applaunch-event.topic}'}", groupId = "#{'${pubsub.applaunch-event.group-id}'}", containerFactory = "appLaunchContainerFactory")
	public void handle(String message) {
		AppLaunchEvent event = gson.fromJson(message, AppLaunchEvent.class);
		userStatsService.processAppLaunchEvent(event);
	}
}
