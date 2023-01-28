/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangalgames.userstats.enums.Result;
import com.dangalgames.userstats.model.UserStats;
import com.dangalgames.userstats.model.request.BulkFetchRequest;
import com.dangalgames.userstats.model.request.MarkSuspiciousUserRequest;
import com.dangalgames.userstats.model.response.BulkUserCashStatsResponse;
import com.dangalgames.userstats.model.response.DepositDetailsResponse;
import com.dangalgames.userstats.model.response.Response;
import com.dangalgames.userstats.model.response.SupportContactDetail;
import com.dangalgames.userstats.model.response.UserDetailsResponse;
import com.dangalgames.userstats.model.response.UserLoginStreakResponse;
import com.dangalgames.userstats.model.response.WithdrawDetailsResponse;
import com.dangalgames.userstats.service.UserStatsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

/**
 * @author Ritika A.
 *
 */
@CrossOrigin
@RestController
@Tag(name = "User stats Controller")
@RequestMapping("/v1/user/")
public class UserStatsController {
	
	@Autowired
	private UserStatsService service;

	@Operation(description = "Get user total free games", responses = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping("game/cash/{userId}")
	public Mono<ResponseEntity<UserStats>> userCashGameCount(@PathVariable String userId) {
		return service.userGameCount(userId).flatMap(response -> {
			if (Result.SUCCESS == response.getResult()) {
				return Mono.just(ResponseEntity.ok(response));
			} 

			return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
		});
	}
	
	@Operation(description = "Get Deposit Details", responses = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping("depsitdetails/{userId}")
	public Mono<ResponseEntity<DepositDetailsResponse>> getDepositDetails(@PathVariable UUID userId) {
		return service.getDepositDetails(userId)
				.flatMap(response -> {
					if(Result.DB_ERROR == response.getResult()) {
						return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
					}else {
						return Mono.just(ResponseEntity.ok(response));
					}
				});
	}
	
	@Operation(description = "Get Withdrawal Details", responses = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping("withdrawaldetails/{userId}")
	public Mono<ResponseEntity<WithdrawDetailsResponse>> getWithdrawalDetails(@PathVariable UUID userId) {
		return service.getWithdrawlDetails(userId)
				.flatMap(response -> {
					if(Result.DB_ERROR == response.getResult()) {
						return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
					}else {
						return Mono.just(ResponseEntity.ok(response));
					}
				});
	}
	
	@PutMapping("suspicious")
	public Mono<ResponseEntity<Response>> markSuspiciousUser(@Valid @RequestBody MarkSuspiciousUserRequest request) {
		return service.markSuspiciousUser(request)
				.flatMap(response -> {
					if(response.getResult() == Result.DB_ERROR)
						return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
					
					return Mono.just(ResponseEntity.ok(response));
				});
	}
	
	@Operation(description = "Get User Details", responses = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping("userdetails/{userId}")
	public Mono<ResponseEntity<UserDetailsResponse>> getUserDetails(@PathVariable UUID userId) {

		return service.getUserDetails(userId).flatMap(response -> {
			if (Result.DB_ERROR == response.getResult()) {
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
			} else {
				return Mono.just(ResponseEntity.ok(response));
			}
		});
	}
	
	@PutMapping("fraud/{userId}")
	public Mono<ResponseEntity<Response>> markUserFraud(@PathVariable UUID userId) {
		return service.markUserFraud(userId)
				.flatMap(response -> {
					if(response.getResult() == Result.DB_ERROR)
						return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
					
					return Mono.just(ResponseEntity.ok(response));
				});
	}
	
	@PostMapping("bulk/stats")
	public Mono<ResponseEntity<BulkUserCashStatsResponse>> getUserCashStatsInBulk(@RequestBody BulkFetchRequest request) {
		return service.getUserCashStatsInBulk(request.getUserIds())
				.flatMap(response -> Result.DB_ERROR == response.getResult() 
						? Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response))
						: Mono.just(ResponseEntity.ok(response)));
	}
	
	@GetMapping("support/{userId}")
	public Mono<ResponseEntity<SupportContactDetail>> fetchSupportContactDetails(@PathVariable UUID userId) {
		return service.fetchSupportContactDetails(userId)
				.flatMap(response -> response.getResult() == Result.DB_ERROR
						? Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response))
						: Mono.just(ResponseEntity.ok(response)));
	}
	
	@GetMapping("streak/{userId}")
	public Mono<ResponseEntity<UserLoginStreakResponse>> fetchUserLoginStreak(@PathVariable UUID userId) {
		return service.fetchUserLoginStreak(userId)
				.flatMap(response -> response.getResult() == Result.DB_ERROR
						? Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response))
						: Mono.just(ResponseEntity.ok(response)));
	}
	
}