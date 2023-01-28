/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model;

import com.dangalgames.userstats.enums.Result;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Ritika A.
 *
 */
@Data
@AllArgsConstructor
public class UserStats {
	
	private Result result;
	private int count;
	
	public UserStats(Result result) {
		this.result = result;
	}

}