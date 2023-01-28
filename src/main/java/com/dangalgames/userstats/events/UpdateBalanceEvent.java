/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.events;

import java.util.Map;
import java.util.UUID;

import lombok.Data;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Data
public class UpdateBalanceEvent {
	
	private UUID userId;
	private double depositBalance;
	private double withdrawalBalance;
	private double lockedBonusBalance;
	private double promoBonusBalance;
	private double pokerBalance;
	private Map<String, Object> metaData;

}
