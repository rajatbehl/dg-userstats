/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model.request;

import java.util.Set;
import java.util.UUID;

import lombok.Data;

/**
 * 
 * @author rajat behl
 * @author Ritika A.
 *
 */
@Data
public class BulkFetchRequest {
	
	private Set<UUID> userIds;

}
