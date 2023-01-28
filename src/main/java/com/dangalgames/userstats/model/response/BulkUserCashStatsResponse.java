/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model.response;

import java.util.Map;
import java.util.UUID;

import com.dangalgames.userstats.enums.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rajat behl
 * @author Ritika A.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUserCashStatsResponse {
	
	private Result result;
	private Map<UUID, UserCashStats> userCashStatsMapping;
	
	public BulkUserCashStatsResponse(Result result) {
		this.result = result;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserCashStats {
		private String firstGamePlayed;
		private String gamesTried;
		private double totalDepositAmount;
		private double totalWithdrawAmount;
		private double totalRake;
		private long totalGamesPlayed;
	}

}
