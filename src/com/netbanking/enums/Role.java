package com.netbanking.enums;

public enum Role {
	CUSTOMER(0), EMPLOYEE(1), MANAGER(2);
	
	private final int index;
	
	private Role(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
}
