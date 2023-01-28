/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.service;

import java.net.URI;
import java.time.Duration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.dangalgames.userstats.config.Config;
import com.dangalgames.userstats.config.ConsulConfig;
import com.dangalgames.userstats.enums.Result;
import com.dangalgames.userstats.model.request.CreditCurrencyRequest;
import com.dangalgames.userstats.model.response.CreditCurrencyResponse;
import com.dangalgames.userstats.utils.Constant;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
	
	@Autowired
	private Config config;
	
	@Autowired
	private ConsulConfig consulConfig;
	
	private WebClient webClient;
	
	@PostConstruct
	public void init() {
		ConnectionProvider provider = ConnectionProvider.builder("fixed")
				.maxConnections(consulConfig.getInt("webclient.maxConnections", config.getMaxConnections()))
				.maxIdleTime(Duration.ofSeconds(consulConfig.getLong("webclient.maxIdleTime", config.getMaxIdleTime())))
				.maxLifeTime(Duration.ofSeconds(consulConfig.getLong("webclient.maxLifeTime", config.getMaxLifeTime())))
				.pendingAcquireTimeout(Duration.ofSeconds(consulConfig.getLong("webclient.pendingAcquireTimeout", config.getPendingAcquireTimeout())))
				.evictInBackground(Duration.ofSeconds(consulConfig.getLong("webclient.evictInBackground", config.getEvictInBackground()))).build();

		this.webClient = WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)))
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
	}
	
	@Override
	public Mono<CreditCurrencyResponse> creditCurrency(CreditCurrencyRequest request) {
		log.info("received request to credit currency: {}", request);
		String url = consulConfig.getString(Constant.CREDIT_USER_WALLET_KEY, config.getCreditUserWalletUrl());
		
		return webClient.put().uri(URI.create(url)).body(Mono.just(request), CreditCurrencyRequest.class)
				.exchangeToMono(response -> response.bodyToMono(CreditCurrencyResponse.class))
				.doOnError(error -> log.error("error occured while calling wallet service to credit currency: {}", request, error))
				.onErrorReturn(new CreditCurrencyResponse(Result.WALLET_SERVICE_NOT_REACHABLE));
	}

}
