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
import com.netbanking.util.Validator;

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
	
	public List<Map<String, Object>> getUserAccounts(String findField, Long findData, Long userId, String role, Long branchId) throws IOException, CustomException
	{
		FunctionHandler functionHandler = new FunctionHandler();
		return functionHandler.getAccounts(userId, role, branchId, findField, findData);
	}
	
	public void initiateTransaction(HttpServletRequest request, Long userId, String role, Long branchId) throws Exception, CustomException {
		StringBuilder jsonBody = new StringBuilder();
		String line;
		try(BufferedReader reader = request.getReader())
		{
			while((line = reader.readLine()) != null)
			{
				jsonBody.append(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		JsonObject jsonObject = JsonParser.parseString(jsonBody.toString()).getAsJsonObject();
		Long fromAccount=null, toAccount=null;
		Float amount=null;
		String transactionType = null;
		try {
			fromAccount = jsonObject.get("fromAccount").getAsLong();
			toAccount = jsonObject.has("toAccount") && !jsonObject.get("toAccount").isJsonNull() 
	                ? jsonObject.get("toAccount").getAsLong() : null;
            transactionType = jsonObject.has("transactionType") && !jsonObject.get("transactionType").isJsonNull() 
            		? jsonObject.get("transactionType").getAsString() : null;
			amount = jsonObject.get("amount").getAsFloat();
		} catch (Exception e) {
			throw new CustomException("Enter numeric values for account number and amount.");
		}
		FunctionHandler functionHandler = new FunctionHandler();
				
		if(!functionHandler.accountAccessPermit(fromAccount, userId, role, branchId)) {
			throw new CustomException("You don't have permission to access this account.");
		}
		
		String fromAccountStatus = functionHandler.getAccountStatus(fromAccount);
		String toAccountStatus = functionHandler.getAccountStatus(toAccount);
		
		if(!fromAccountStatus.equals("ACTIVE"))
		{
			throw new CustomException("Sender Account is "+ fromAccountStatus);
		}
		if(!toAccountStatus.equals("ACTIVE"))
		{
			throw new CustomException("Reciever Account is "+ toAccountStatus);
		}
		
		if(fromAccount.equals(toAccount))
		{
			throw new CustomException("Cannot send money to the same account.");
		}
		if(amount<0)
		{
			throw new CustomException("Amount cannot be negative.");
		}
		float decimalPart = amount - amount.intValue();
		if(decimalPart!=0&&decimalPart<0.01)
		{
			throw new CustomException("Cannot send amount less than 0.01 rupees.");
		}
		if(!Validator.decimalChecker(amount))
		{
			throw new CustomException("Can have only 2 digits after the decimal.");
		}
		if(transactionType.equals("same-bank")||transactionType.equals("other-bank"))
		{
			if(toAccount==null)
			{				
				throw new CustomException("Reciever account is required.");
			}
			functionHandler.makeTransaction(fromAccount, toAccount, userId, amount, transactionType);
			return;
		} else {
			functionHandler.makeTransaction(fromAccount, null, userId, amount, transactionType);
		}
	}
	
	public List<Map<String, Object>> getStatement(HttpServletRequest request, Long userId, String role, Long branchId) throws CustomException {
		StringBuilder jsonBody = new StringBuilder();
		String line;
		try(BufferedReader reader = request.getReader())
		{
			while((line = reader.readLine()) != null)
			{
				jsonBody.append(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		JsonObject jsonObject = JsonParser.parseString(jsonBody.toString()).getAsJsonObject();
		Long accountNumber = jsonObject.has("accountNumber") && !jsonObject.get("accountNumber").isJsonNull() 
                ? jsonObject.get("accountNumber").getAsLong() 
                : null;

		Long fromDate = jsonObject.has("fromDate") && !jsonObject.get("fromDate").isJsonNull() 
		           ? jsonObject.get("fromDate").getAsLong() 
		           : null;
		
		Long toDate = jsonObject.has("toDate") && !jsonObject.get("toDate").isJsonNull() 
		         ? jsonObject.get("toDate").getAsLong() 
		         : null;

		Integer limit = jsonObject.has("limit") && !jsonObject.get("limit").isJsonNull() 
                ? jsonObject.get("limit").getAsInt() 
                : null;
		FunctionHandler functionHandler = new FunctionHandler();
		
		if(!functionHandler.accountAccessPermit(accountNumber, userId, role, branchId)) {
			throw new CustomException("You don't have permission to access this account.");
		}
		
		String accountStatus = functionHandler.getAccountStatus(accountNumber);
		if(accountStatus.equals("BLOCKED")||accountStatus.equals("INACTIVE"))
		{
			throw new CustomException("Account is "+accountStatus+".");
		}
		
		if(fromDate==null&&toDate==null&&limit==null)
		{
			throw new CustomException("Please enter valid details");
		}
		if((fromDate==null&&toDate!=null)||(fromDate!=null&&toDate==null)) {
			throw new CustomException("Time frame is invalid.");
		}
		if(fromDate!=null && fromDate>toDate)
		{
			throw new CustomException("Time frame is invalid.");
		}
		
		try {
			return functionHandler.getTransactionStatement(accountNumber, fromDate, toDate, limit);
		} catch (CustomException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Map<String, Object> getProfile(Long userId, String role) throws CustomException
	{
		FunctionHandler functionHandler = new FunctionHandler();
		return functionHandler.getProfile(userId, role);
	}
	
	public void initiateAction(HttpServletRequest request, Long userId, String role, Long branchId) throws Exception, CustomException {
		StringBuilder jsonBody = new StringBuilder();
		String line;
		try(BufferedReader reader = request.getReader())
		{
			while((line = reader.readLine()) != null)
			{
				jsonBody.append(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		JsonObject jsonObject = JsonParser.parseString(jsonBody.toString()).getAsJsonObject();
		Long accountNumber=null;
		String actionType = null;
		
		try {
			accountNumber = jsonObject.get("accountNumber").getAsLong();
            actionType = jsonObject.has("actionType") && !jsonObject.get("actionType").isJsonNull() 
            		? jsonObject.get("actionType").getAsString() : null;
        } catch (Exception e) {
			throw new CustomException("Enter numeric values for account number.");
		}
		
		FunctionHandler functionHandler = new FunctionHandler();
		
		if(!functionHandler.accountAccessPermit(accountNumber, userId, role, branchId)) {
			throw new CustomException("You don't have permission to access this account.");
		}
		
		if(!actionType.equals("BLOCK")&&!actionType.equals("UNBLOCK")&&!actionType.equals("DELETE")) {
			throw new CustomException("Invalid action type.");
		}
		
		functionHandler.actionHandler(actionType, "ACCOUNT", accountNumber);
	}
}