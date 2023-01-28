/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.cache.ConsulCache.Listener;
import com.orbitz.consul.cache.KVCache;
import com.orbitz.consul.model.kv.Value;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author rajat behl
 *
 */
@Configuration
@Slf4j
public class ConsulConfig {

	@Autowired
	private Config config;

	private KVCache cache;
	private KeyValueClient keyValueClient;
	/**
	 * This map will hold the key-value mapping stored in consul and will get
	 * updated if there is some updates in consul.
	 */
	private Map<String, Object> configs = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		log.info("initializing consul config store with url {} and path {}", config.getConsulURL(),
				config.getConsulPath());
		try {
			Consul client = Consul.builder().withUrl(config.getConsulURL()).build();
			keyValueClient = client.keyValueClient();
			cache = KVCache.newCache(keyValueClient, config.getConsulPath());
			cache.addListener(consulListener());
			cache.start();
		} catch (Exception ex) {
			log.error("Error while starting consul cache", ex);
		}

	}

	public String getString(String key, String defaultValue) {
		configs.computeIfAbsent(key, k -> defaultValue);
		return String.valueOf(configs.get(key));
	}

	public double getDouble(String key, double defaultValue) {
		configs.computeIfAbsent(key, k -> defaultValue);
		double value = 0;
		try {
			value = Double.parseDouble(String.valueOf(configs.get(key)));
		} catch (NumberFormatException ex) {
			log.error("invalid int value : " + value);
		}

		return value;
	}
	
	public int getInt(String key, int defaultValue) {
		configs.computeIfAbsent(key, k -> defaultValue);
		int value = 0;
		try {
			value = Integer.parseInt(String.valueOf(configs.get(key)));
		} catch (NumberFormatException ex) {
			log.error("invalid int value : " + value);
		}

		return value;
	}
	
	public long getLong(String key, long defaultValue) {
		configs.computeIfAbsent(key, k -> defaultValue);
		long value = 0;
		try {
			value = Long.parseLong(String.valueOf(configs.get(key)));
		}catch(NumberFormatException ex) {
			log.error("invalid long value : " + value);
		}
		
		return value;
	}
	
	@PreDestroy
	public void destroy() {
		try {
			if (cache != null)
				cache.stop();
		} catch (Exception ex) {
			log.error("Error while stopping consul cache", ex);
		} finally {
			cache = null;
			keyValueClient = null;
		}

	}

	private Listener<String, Value> consulListener() {
		return newValues -> {
			log.info("received callback for consul property changes...");
			newValues.values().stream().forEach(keyValue -> keyValue.getValueAsString().ifPresent(value -> {
				String key = keyValue.getKey().replace(config.getConsulPath(), "");// To trim out the path
				if (!key.isEmpty()) {
					log.info("Key: " + key + " Value: " + value);
					configs.put(key, value);
				}
			}));
		};
	}

}
