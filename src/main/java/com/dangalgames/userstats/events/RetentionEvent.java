/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.events;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rajat behl
 *
 */
@Data
@NoArgsConstructor
public class RetentionEvent {

	private UUID userId;
	private String name;
	private String lastGamePlayed;
	private String lastGamePlayedTime;
	private String lastGameResult;
	private String lastDepositAmount;
	private String lastDepositTime;
	private String lastSeen;
	private long timestamp = Instant.now().getEpochSecond();
	private Map<String, Object> userProperties;
	private List<Event> events;
	
	public RetentionEvent(UUID userId) {
		this.userId = userId;
	}
	
	public RetentionEvent(UUID userId, String name) {
		this.userId = userId;
		this.name = name;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Event {
		private String eventName;
		private Map<String, Object> attributes;
	}
	
}
