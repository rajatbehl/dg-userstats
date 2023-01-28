/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 
 * @author Nidhi S. Kushwaha
 *
 */
@Configuration
public class ThreadPoolConfig {
	
	@Autowired
	private Config config;
	
	@Bean(name = "db_executor")
	public TaskExecutor dbExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getDbAsyncCorePoolSize());
        executor.setMaxPoolSize(config.getDbAsyncMaxPoolSize());
        executor.setThreadNamePrefix("async-db-reponse");
        executor.initialize();
        return executor;
	}
	
	@Bean(name = "event_executor")
	public TaskExecutor eventExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getEventAsyncCorePoolSize());
        executor.setMaxPoolSize(config.getEventAsyncMaxPoolSize());
        executor.setThreadNamePrefix("async-event-reponse");
        executor.initialize();
        return executor;
	}
}