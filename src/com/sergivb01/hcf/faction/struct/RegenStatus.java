package com.sergivb01.hcf.faction.struct;

import org.bukkit.ChatColor;

public enum RegenStatus{
	FULL(ChatColor.GREEN.toString() + ' '),
	REGENERATING(ChatColor.GREEN.toString() + '^'),
	PAUSED(ChatColor.RED.toString() + '<');

//    FULL(ChatColor.GREEN.toString() + '\u25b6'),
//    REGENERATING(ChatColor.GOLD.toString() + '\u25b2'),
//    PAUSED(ChatColor.RED.toString() + '\u25a0');

	private final String symbol;

	RegenStatus(String symbol){
		this.symbol = symbol;
	}

	public String getSymbol(){
		return this.symbol;
	}
}

