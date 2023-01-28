/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model;

import java.util.UUID;

import lombok.Data;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Data
public class UserGameCashStats {
	
	private UUID userId;
	private int lifeTimeCashGamesWon;
	private int lifeTimeCashGamesLost;
	private double lifeTimeWithdrawalAmount;
	private double lifeTimeDepositAmount;
	private double withdrawalBalance;
	private double depositBalance;
	private double lifeTimeRummyRake;
	private double lifeTimeRake;
	private String lastGamePlayedResult;
	private double lifeTimeRummyCashTourneyWinnings;
	private double lifeTimePokerCashTourneyWinnings;

}
