/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.health;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 
 * @author rajat behl
 *
 */
@Component
@Slf4j
public class CustomHealthIndicator implements ReactiveHealthIndicator{

	private static final String SERVICE = "USER_STATS_SERVICE";
	
	@Autowired
	private List<HealthIndicator> healthIndicators;
	
	@Override
	public Mono<Health> health() {
		Map<String, String> healthStatuses = new HashMap<>();
		for(HealthIndicator current : healthIndicators) {
			if(!(current instanceof CustomHealthIndicator)) {
				healthStatuses.put(current.getClass().getSimpleName(), current.health().toString());
			}
		}
		
		log.info("health indicator status {}", healthStatuses);
		return Mono.just(Health.up().withDetail(SERVICE, "Available").build());
	}
}
