/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model;

import java.util.UUID;


import lombok.Data;

/**
 * @author Nidhi S. Kushwaha
 *
 */
@Data
public class RegistrationEvent {

	private UUID userId;
	
}
