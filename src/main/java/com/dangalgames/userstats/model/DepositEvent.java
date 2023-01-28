/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Nidhi S. Kushwaha
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositEvent {

	private String userId;
	private Double amount;
	private long depositFulfillDate;
	private long timestamp;
}
