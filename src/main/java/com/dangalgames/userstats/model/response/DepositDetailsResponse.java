/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model.response;

import com.dangalgames.userstats.enums.Result;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Data
@NoArgsConstructor
public class DepositDetailsResponse {

	private Result result;
	private double totalDepositAmount;
	private int totalDepositCount;
	
	public DepositDetailsResponse(Result result) {
		this.result = result;
	}
	
}
