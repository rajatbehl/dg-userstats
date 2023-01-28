/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.service;

import com.dangalgames.userstats.model.request.CreditCurrencyRequest;
import com.dangalgames.userstats.model.response.CreditCurrencyResponse;

import reactor.core.publisher.Mono;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
public interface WalletService {
	
	Mono<CreditCurrencyResponse> creditCurrency(CreditCurrencyRequest request);
	
}
