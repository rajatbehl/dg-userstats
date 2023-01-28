/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author rajat behl
 *
 */
public enum Game {

	CALLBREAK("1", "Callbreak"),
	POOL("2", "Pool"),
	QUIZ("3", "Quiz"),
	POKER("4", "Poker"),
	FANTASY("5", "Fantasy"),
	STREET_RACER("6", "Street Racer"),
	FRUIT_SPLIT("7", "Fruit Split"),
	RUNNER_NUMBER1("8", "Runner Number One"),
	BUBBLE_SHOOTER("9", "Bubble Shooter"),
	RUMMY("10", "Rummy"),
	KNIFECUT("11", "Knife Cut"),
	ARCHERY("12", "Archery"),
	CARROM("13","Carrom"),
	LUDO("14", "LUDO"),
	CANDY_CRUSH("15", "Candy Crush");
	
	
	private String id;
	private String gameName;
	
	private static Map<String, String> gameIdByNameMapping = new HashMap<>();
	
	static {
		for(Game game : Game.values()) {
			gameIdByNameMapping.put(game.getGameName(), game.getId());
		}
	}
	
	private Game(String id, String gameName) {
		this.id = id;
		this.gameName = gameName;
	}
	
	public String getId() {
		return id;
	}
	
	public String getGameName() {
		return gameName;
	}
	
	public static String getIdByName(String gameName) {
		return gameIdByNameMapping.get(gameName);
	}
	
}
