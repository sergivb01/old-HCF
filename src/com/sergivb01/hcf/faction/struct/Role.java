package com.sergivb01.hcf.faction.struct;

public enum Role{
	LEADER("Leader", "**"),
	COLEADER("Co-Leader", "**"),
	CAPTAIN("Captain", "*"),
	MEMBER("Member", "");

	private final String name;
	private final String astrix;

	Role(String name, String astrix){
		this.name = name;
		this.astrix = astrix;
	}

	public String getName(){
		return this.name;
	}

	public String getAstrix(){
		return this.astrix;
	}
}

