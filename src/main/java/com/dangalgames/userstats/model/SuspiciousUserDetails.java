package com.dangalgames.userstats.model;

import java.sql.Timestamp;

import com.dangalgames.userstats.enums.Result;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuspiciousUserDetails {
	
	private Result result = Result.SUCCESS;
	private double totalDepositAmount;
	private double totalWithdrawalAmount;
	private double totalRake;
	private double lifetimeDepositCount;
	private double lifetimeAttemptedDepositCount;
	private Timestamp lastManualWithdrawalTime;
	
	public SuspiciousUserDetails(Result result) {
		this.result = result;
	}
}
