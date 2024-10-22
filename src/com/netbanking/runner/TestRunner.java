package com.netbanking.runner;

import com.netbanking.api.Api;

public class TestRunner {
    public static void main(String[] args) {
    	Api api = new Api();
    	// Get Login
    	api.getLogin(1L, "password");
    	
    	// Get Account
    	api.getAccounts(1L, "CUSTOMER", null);
    	api.getAccounts(null, "EMPLOYEE", 101L);
    	api.getAccounts(null, "MANAGER", null);
    	
    	// Get Transaciton
    	api.getTransactions(1L, 2L, 4L);
    }
}
