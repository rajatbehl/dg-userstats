package com.dangalgames.userstats.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class WithdrawalEvent {
	
	private UUID userId;
	private double amount;
	private LocalDateTime createdAt;
	private int withdrawalStatus;
	private LocalDateTime manualProcessedTime;

}
