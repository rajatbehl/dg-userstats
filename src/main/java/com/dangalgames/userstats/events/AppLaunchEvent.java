/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.events;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rajat behl
 *
 */
@Data
@NoArgsConstructor
public class AppLaunchEvent {

	private UUID userId;
	private long timestamp;
	
}
