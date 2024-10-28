package com.netbanking.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;

public class ApiHandler {
	public Map<String, Object> loginHandler(HttpServletRequest request) throws IOException, CustomException
	{
		try(BufferedReader reader = request.getReader())
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
			return map;
		}
	}
	
	public List<Map<String, Object>> getUserAccounts(Long userId, String role, Long branchId) throws IOException, CustomException
	{
		FunctionHandler functionHandler = new FunctionHandler();
		return functionHandler.getAccounts(userId, role, branchId);
	}
	
	public void initiateTransaction(HttpServletRequest request, Long userId) throws Exception {
		StringBuilder jsonBody = new StringBuilder();
		String line;
		try(BufferedReader reader = request.getReader())
		{
			System.out.println("data");
			while((line = reader.readLine()) != null)
			{
				jsonBody.append(line);
				System.out.println(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		JsonObject jsonObject = JsonParser.parseString(jsonBody.toString()).getAsJsonObject();
		Long fromAccount = jsonObject.get("fromAccount").getAsLong();
		Long toAccount = jsonObject.get("toAccount").getAsLong();
		Float amount = jsonObject.get("amount").getAsFloat();
		String bankName = jsonObject.get("bankName").getAsString();
		if(fromAccount.equals(toAccount))
		{
			System.out.println(fromAccount+" "+toAccount);
			throw new CustomException("Cannot send money to the same account.");
		}
		if(bankName.trim().isEmpty())
		{
			bankName=null;
		}
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.makeTransaction(fromAccount, toAccount, userId, amount, bankName);
	}
	
	public List<Map<String, Object>> getStatement(HttpServletRequest request) throws CustomException {
		StringBuilder jsonBody = new StringBuilder();
		String line;
		try(BufferedReader reader = request.getReader())
		{
			System.out.println("data");
			while((line = reader.readLine()) != null)
			{
				jsonBody.append(line);
				System.out.println(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		JsonObject jsonObject = JsonParser.parseString(jsonBody.toString()).getAsJsonObject();
		Long accountNumber = jsonObject.get("accountNumber").getAsLong();
		Long fromDate = jsonObject.get("fromDate").getAsLong();
		Long toDate = jsonObject.get("toDate").getAsLong();
		FunctionHandler functionHandler = new FunctionHandler();
		try {
			return functionHandler.getTransactionStatement(accountNumber, fromDate, toDate);
		} catch (CustomException e) {
			e.printStackTrace();
			throw e;
		}
	}
}