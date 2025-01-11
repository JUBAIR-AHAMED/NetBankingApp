package com.netbanking.enums;

public enum Status {
	ACTIVE(0), INACTIVE(1), BLOCKED(2);
	
	private final int index;
	
	private Status(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
}
