package com.dangalgames.userstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UserStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserStatsApplication.class, args);
	}

}
