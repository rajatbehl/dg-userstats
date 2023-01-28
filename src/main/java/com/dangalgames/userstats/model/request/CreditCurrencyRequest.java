/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Data
public class CreditCurrencyRequest {
	
	private UUID userId;
	private String transactionType;
	private List<Operation> operations;
	private boolean publishTransaction = true;
	private String description;
	
	@Data
	@AllArgsConstructor
	public static class Operation {
		private String walletName;
		private double amount;
	}

}
