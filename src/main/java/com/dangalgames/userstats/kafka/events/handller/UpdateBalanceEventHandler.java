/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.kafka.events.handller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dangalgames.userstats.events.UpdateBalanceEvent;
import com.dangalgames.userstats.service.UserStatsService;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Slf4j
@Service
public class UpdateBalanceEventHandler {
	
	@Autowired
	private UserStatsService userStatsService;

	private Gson gson = new Gson();
	
	@KafkaListener(topics = "#{'${pubsub.update-balance.topic}'}", groupId = "#{'${pubsub.update-balance.group-id}'}", containerFactory = "updateBalanceContainerFactory")
	public void handle(String message) {
		
		if(Strings.isNullOrEmpty(message)) {
			log.warn("Received null/empty message for user balance update!");
			return;
		}
		
		UpdateBalanceEvent event = gson.fromJson(message, UpdateBalanceEvent.class);
		userStatsService.updateUserBalance(event);
	}

}
