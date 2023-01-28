/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model.response;

import com.dangalgames.userstats.enums.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginStreakResponse {
	
	private Result result;
	private int userDailyLoginStreak;
	private int applicableDailyLoginStreak;
	private double totalIncentiveAmount;
	private double maxIncentiveAmount;
	
	public UserLoginStreakResponse(Result result) {
		this.result = result;
	}

}
