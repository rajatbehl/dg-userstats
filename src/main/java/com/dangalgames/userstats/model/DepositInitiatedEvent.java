/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ritika A.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositInitiatedEvent {

	private UUID userId;
	private double amount;
	private String bonusId;	
	
}
