/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.utils;

import java.util.regex.Pattern;

public class Constant {
	
	private Constant() {}
	
	public static final String FAILED = "Failed";
	public static final String UNDERSCORE = "_";
	public static final String TRUE = "t";
	public static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_");
	public static final String CASH_GAME_WIN_PERCENTAGE = "Cash Game Win Percentage";
	public static final String PNL = "PNL";
	public static final String TOTAL_RUMMY_RAKE = "Total Rummy Rake";
	public static final String TOTAL_RAKE = "Total Rake";
	public static final String RAKE_TO_DEPOSIT_PERCENTAGE = "Rake to Deposit Percentage";
	public static final String TOTAL_RUMMY_CASH_TOURNEY_WINNINGS = "Total Rummy Cash Tourney Winnings";
	public static final String TOTAL_POKER_CASH_TOURNEY_WINNINGS = "Total Poker Cash Tourney Winnings";
	public static final String TOTAL_CASH_TOURNEY_WINNINGS = "Total Cash Tourney Winnings";
	public static final String LAST_CASH_GAME_RESULT = "Last Cash Game Result";
	public static final String GAME_RESULT = "game_result";
	public static final String LOGIN_INCENTIVE = "Login Incentive";
	public static final String LOGIN_INCENTIVE_DESCRIPTION = "Login incentive of Rs. %s credited.";
	
	//Wallets
	public static final String DEPOSIT = "deposit";
	public static final String WITHDRAWAL = "withdrawal";
	public static final String PROMO_BONUS = "promo_bonus";
	public static final String LOCKED_BONUS = "locked_bonus";
	
	//Keys
	public static final String LOGIN_STREAK_DAYS_KEY = "streak.login.days";
	public static final String LOGIN_STREAK_DEPOSIT_INCENTIVE_KEY = "streak.login.incentive.deposit";
	public static final String LOGIN_STREAK_WITHDRAWAL_INCENTIVE_KEY = "streak.login.incentive.withdrawal";
	public static final String LOGIN_STREAK_PROMO_INCENTIVE_KEY = "streak.login.incentive.promo-bonus";
	public static final String LOGIN_STREAK_LOCKED_INCENTIVE_KEY = "streak.login.incentive.locked-bonus";
	public static final String LOGIN_STREAK_MAX_INCENTIVE_KEY = "streak.login.incentive.max";
	public static final String CREDIT_USER_WALLET_KEY = "endpoints.wallet.credit";

}
