package com.netbanking.enums;

public enum AccountType {
	SAVINGS(0), CURRENT(1);
	
	private final int index;
	
	private AccountType(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
}
