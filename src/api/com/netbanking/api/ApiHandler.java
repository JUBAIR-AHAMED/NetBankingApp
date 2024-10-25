package com.netbanking.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;

public class ApiHandler {
	public Map<String, Object> loginHandler(BufferedReader reader) throws IOException, CustomException
	{
		JsonObject jsonObject = Parser.getJsonObject(reader);
    	
    	Long username = jsonObject.get("username").getAsLong();
    	String password = jsonObject.get("password").getAsString();
    	FunctionHandler fn = new FunctionHandler();
        Map<String, Object> map = fn.getLogin(username, password);
        String role = (String) map.get("role");
        Long user_id = (Long) map.get("userId");
        System.out.println(map);
        FunctionHandler functionalHandler = new FunctionHandler();
        if(role.equals("EMPLOYEE")||role.equals("MANAGER")) {
        	map.putAll(functionalHandler.getEmployee(user_id));
        }
        else if(role.equals("CUSTOMER"))
        {
        	map.putAll(functionalHandler.getCustomer(user_id));
        }
        System.out.println(map);
        return map;
	}
	
	public List<Map<String, Object>> getUserAccounts(Long userId, String role, Long branchId) throws IOException, CustomException
	{
		FunctionHandler functionHandler = new FunctionHandler();
		return functionHandler.getAccounts(userId, role, branchId);
	}
}