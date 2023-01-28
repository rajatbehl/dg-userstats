package com.dangalgames.userstats.model.response;

import com.dangalgames.userstats.enums.Result;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserDetailsResponse {
	
	private Result result;
	private Double lifetimeDepositAmount;
	private Boolean isUserSuspicious;
	
	public UserDetailsResponse(Result result) {
		this.result = result;
	}

}
