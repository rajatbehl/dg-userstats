package com.dangalgames.userstats.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGameCashStatsEvent {
	
	private String userId;
	private double winnings;
	private double rake;
	private String game;
	private long currentTime;
	private boolean isTourney;
	
}
