/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model;

import java.util.List;

import com.dangalgames.userstats.model.request.CreditCurrencyRequest.Operation;

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
public class CreditOperation {
	
	private List<Operation> operations;
	private double totalAmountToCredit;
	
}
