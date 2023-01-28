/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model;

import lombok.Data;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Data
public class LastGameDetails {
	
	private String lastGamePlayed;
	private String lastGameResult;
	private String lastGamePlayedTime;
}
