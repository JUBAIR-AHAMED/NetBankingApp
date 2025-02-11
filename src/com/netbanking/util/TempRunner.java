package com.netbanking.util;

import com.netbanking.enums.Role;

public class TempRunner {
	public static void main(String[] args) {
		Role role = Role.CUSTOMER;
		System.out.println(role.name());
	}
}