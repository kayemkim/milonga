package com.km.milonga.rhino;

public class Player {
	
	private String playerName;

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String homerun() {
		return playerName + "'s homer!!!";
	}
	
}
