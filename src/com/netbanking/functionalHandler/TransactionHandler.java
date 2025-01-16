package com.netbanking.functionalHandler;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.model.Model;
import com.netbanking.object.Account;
import com.netbanking.object.Branch;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.object.User;
import com.netbanking.util.Converter;
import com.netbanking.util.Parser;
import com.netbanking.util.Redis;
import com.netbanking.util.Validator;

public class TransactionHandler {
	public <T extends Model> Map<String, Object> get(Long userId, Class<T> clazz) throws CustomException, Exception
	{
		String cacheKey=null;
		if(clazz.equals(User.class)) {
			cacheKey = "USER$USER_ID:";
		} else if (clazz.equals(Customer.class)) {
			cacheKey = "CUSTOMER$USER_ID:";
		} else if (clazz.equals(Employee.class)) {
			cacheKey = "EMPLOYEE$USER_ID:";
		} else if (clazz.equals(Account.class)) {
			cacheKey = "ACCOUNT$ACCOUNT_NUMBER:";
		} else if (clazz.equals(Branch.class)) {
			cacheKey = "BRANCH$BRANCH_ID:";
		}
		cacheKey=cacheKey+userId;
		FunctionHandler functionHandler = new FunctionHandler();
		ObjectMapper objectMapper = new ObjectMapper();
		String cachedData = Redis.get(cacheKey);
		if(cachedData!=null) {
			System.out.println("Retrieved from cache.");
			return objectMapper.readValue(cachedData, Map.class);
		} 
		Map<String, Object> map = functionHandler.getRecord(userId, clazz);
		Redis.setex(cacheKey, map);
		return map;
	}
	
	public List<Map<String, Object>> getStatement(HttpServletRequest request, Long userId, String role, Long branchId) throws CustomException, Exception {
		JsonObject jsonObject = Parser.getJsonObject(request);
		
		Long accountNumber = Parser.getValue(jsonObject, "accountNumber", Long.class, "Account Number", true);
		Long fromDate = Parser.getValue(jsonObject, "fromDate", Long.class, "From Date", false);
		Long toDate = Parser.getValue(jsonObject, "toDate", Long.class, "To Date", false);
		Integer limit = Parser.getValue(jsonObject, "limit", Integer.class, "Limit", false);
		Integer currentPage = Parser.getValue(jsonObject, "currentPage", Integer.class, "Current Page", false);
		Boolean count = Parser.getValue(jsonObject, "count", Boolean.class, "Count", false);
	    
        Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
	            
		FunctionHandler functionHandler = new FunctionHandler();
		
		Map<String, Object> accountMap = get(accountNumber, Account.class);
		
		if(accountMap == null) {
			throw new CustomException(HttpServletResponse.SC_NOT_FOUND,"Account not found.");
		}
		
		if(role.equals("CUSTOMER"))
		{
			if(userId != Converter.convertToLong(accountMap.get("userId"))) {
				throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Permission denied to access this account.");
			}
		} else if(role.equals("EMPLOYEE")) {
			if(branchId != Converter.convertToLong(accountMap.get("branchId"))) {
				throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Operation failed. Employee belongs to a different branch.");
			}
		}
		
		String accountStatus = (String) accountMap.get("status");
		if(!accountStatus.equals("ACTIVE"))
		{
			throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Sender Account is "+ accountStatus);
		}
		
		if(fromDate!=null && toDate!=null && fromDate>toDate)
		{
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Time frame is invalid.");
		}
		
		List<Map<String, Object>> list = functionHandler.getTransactionStatement(accountNumber, fromDate, toDate, limit, offset, count);
		return list;
	}
	
	public void initiateTransaction(HttpServletRequest request, Long userId, String role, Long branchId) throws CustomException, Exception {
		JsonObject jsonObject = Parser.getJsonObject(request);
		Long fromAccount=Parser.getValue(jsonObject, "fromAccount", Long.class, "Sender Account", true);
		Long toAccount=Parser.getValue(jsonObject, "toAccount", Long.class, "Reciver Account", false);
		Float amount=Parser.getValue(jsonObject, "amount", Float.class, "Amount", true);
		String transactionType = Parser.getValue(jsonObject, "transactionType", String.class, "Transaction Type", true);
		System.out.println(transactionType);
		FunctionHandler functionHandler = new FunctionHandler();
		Map<String, Object> fromAccountMap = get(fromAccount, Account.class);
		Map<String, Object> toAccountMap=null;
		if(toAccount!=null) {
			toAccountMap = get(toAccount, Account.class);
		}		
		if(fromAccountMap == null) {
			throw new CustomException(HttpServletResponse.SC_NOT_FOUND, "Sender account not found.");
		}
		if(role.equals("CUSTOMER"))
		{
			if(userId != Converter.convertToLong(fromAccountMap.get("userId"))) {
				throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Permission denied to access this account.");
			}
		} else if(role.equals("EMPLOYEE")) {
			if(branchId != Converter.convertToLong(fromAccountMap.get("branchId"))) {
				throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Operation failed. Employee belongs to a different branch.");
			}
		}
		
		String fromAccountStatus = (String) fromAccountMap.get("status");
		String toAccountStatus = toAccountMap!=null? (String) toAccountMap.get("status"):null;

		if(!fromAccountStatus.equals("ACTIVE"))
		{
			throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Sender Account is "+ fromAccountStatus);
		}
		if(toAccountStatus!=null&&!toAccountStatus.equals("ACTIVE"))
		{
			throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Reciever Account is "+ toAccountStatus);
		}
		
		if(fromAccount.equals(toAccount))
		{
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Cannot send money to the same account.");
		}
		if(amount<0)
		{
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Amount cannot be negative.");
		}
		float decimalPart = amount - amount.intValue();
		if(decimalPart!=0&&decimalPart<0.01)
		{
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Cannot send amount less than 0.01 rupees.");
		}
		if(!Validator.decimalChecker(amount))
		{
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Can have only 2 digits after the decimal.");
		}
		String cacheKeyFromAcc = "ACCOUNT$ACCOUNT_NUMBER:"+fromAccount;
		String cacheKeyToAcc = "ACCOUNT$ACCOUNT_NUMBER:"+toAccount;
		if((transactionType.equals("same-bank")||transactionType.equals("other-bank"))&&toAccount==null)
		{				
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Reciever account is required.");
		}
		functionHandler.makeTransaction(fromAccountMap, toAccountMap, userId, amount, transactionType);
		Redis.delete(cacheKeyFromAcc);
		Redis.delete(cacheKeyToAcc);
	}
}
