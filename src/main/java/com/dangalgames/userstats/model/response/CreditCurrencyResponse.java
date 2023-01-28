/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model.response;

import java.util.List;
import java.util.UUID;

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
public class CreditCurrencyResponse {
	
	private Result result;
	private UUID userId;
	private List<OperationStatus> operationStatus;
	private String transactionType;
	private List<String> disabledWallets;
	
	@Data
	public static class OperationStatus {
		private String walletName;
		private double balance;
		private double requestedAmount;
	}
	
	public CreditCurrencyResponse(Result result) {
		this.result = result;
	}

}
