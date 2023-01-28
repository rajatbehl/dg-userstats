/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.model.request;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.dangalgames.userstats.enums.SuspiciousUserAction;

import lombok.Data;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Data
public class MarkSuspiciousUserRequest {
	
	@NotNull
	private UUID userId;
	
	@NotNull
	private SuspiciousUserAction action;
	
	private String reason;

}
