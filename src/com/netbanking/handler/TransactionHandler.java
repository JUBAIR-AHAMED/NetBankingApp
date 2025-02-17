package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.google.gson.JsonObject;
import com.netbanking.activityLogger.AsyncLoggerUtil;
import com.netbanking.enums.Role;
import com.netbanking.exception.CustomException;
import com.netbanking.functions.AccountFunctions;
import com.netbanking.functions.TransactionFunctions;
import com.netbanking.object.Activity;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Converter;
import com.netbanking.util.Parser;
import com.netbanking.util.RedissonHelper;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Validator;
import com.netbanking.util.Writer;

public class TransactionHandler {
	private static RedissonClient redisson = RedissonHelper.getInstance();

	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		Long branchId = store.getBranchId();
		Role role = store.getRole();
		
		Map<String, Object> responseMap = new HashMap<>();
		Map<String, Object> data = new HashMap<String, Object>();
		JsonObject jsonObject = Parser.getJsonObject(request);
		Parser.storeIfPresent(jsonObject, data, "accountNumber", Long.class, "Account Number", true);
		Parser.storeIfPresent(jsonObject, data, "fromDate", Long.class, "From Date", false);
		Parser.storeIfPresent(jsonObject, data, "toDate", Long.class, "To Date", false);
		Parser.storeIfPresent(jsonObject, data, "limit", Integer.class, "Limit", false);
		Parser.storeIfPresent(jsonObject, data, "currentPage", Integer.class, "Current Page", false);
		Parser.storeIfPresent(jsonObject, data, "count", Boolean.class, "Count", false);
		
		Map<String, Object> accountData = new AccountFunctions().get((Long) data.get("accountNumber"));
		// Validation
		if(Validator.isNull(accountData)) {
			throw new CustomException(HttpServletResponse.SC_NOT_FOUND,"Account not found.");
		}
		
		if(role.equals(Role.CUSTOMER))
		{
			if(userId != Converter.convertToLong(accountData.get("userId"))) {
				throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Permission denied to access this account.");
			}
		} else if(role.equals(Role.EMPLOYEE)) {
			if(branchId != Converter.convertToLong(accountData.get("branchId"))) {
				throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Operation failed. Employee belongs to a different branch.");
			}
		}
		String accountStatus = (String) accountData.get("status");
		if(role.equals(Role.CUSTOMER) && !accountStatus.equals("ACTIVE"))
		{
			throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Sender Account is "+ accountStatus);
		}
		Long fromDate = (Long) data.getOrDefault("fromDate", null);
		Long toDate = (Long) data.getOrDefault("toDate", null);
		if(fromDate!=null && toDate!=null && fromDate>toDate)
		{
			throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Time frame is invalid.");
		}
		
        List<Map<String, Object>> statement = new TransactionFunctions().getStatement(data, accountData);
        Long count = ApiHelper.getCount(statement);
        if(count!=null) {
        	responseMap.put("count", count);
        } else {
        	responseMap.put("statement", statement);
        }
        AsyncLoggerUtil.log(TransactionHandler.class, Level.INFO, "Statement fetched successfully.");
        Writer.responseMapWriter(response, 
        		HttpServletResponse.SC_OK, 
        		HttpServletResponse.SC_OK, 
        		"Statement fetched successfully.", 
        		responseMap);
	}
	
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
		Map<String, Object> responseMap = new HashMap<>();
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		Long branchId = store.getBranchId();
		Role role = store.getRole();
		Map<String, Object> details = new HashMap<String, Object>();
		JsonObject jsonObject = Parser.getJsonObject(request);
		Parser.storeIfPresent(jsonObject, details, "fromAccount", Long.class, "Sender Account", true);
		Parser.storeIfPresent(jsonObject, details, "toAccount", Long.class, "Reciver Account", false);
		Parser.storeIfPresent(jsonObject, details, "amount", Float.class, "Amount", true);
		Parser.storeIfPresent(jsonObject, details, "transactionType", String.class, "Transaction Type", true);
		
		Long fromAccount=Parser.getValue(jsonObject, "fromAccount", Long.class, "Sender Account", true);
		Long toAccount=Parser.getValue(jsonObject, "toAccount", Long.class, "Reciver Account", false);
		Float amount = (Float) details.get("amount");
		String transactionType = (String) details.get("transactionType");
		
		Long fromAccountUserId = null;
		Long toAccountUserId = null;
		
        
        RLock firstLock = null;
        RLock secondLock = null;
        
        try {
        	boolean locked;
        	if (transactionType.equals("same-bank")) {
        		Long lockAccount1 = Math.min(fromAccount, toAccount);
        		Long lockAccount2 = Math.max(fromAccount, toAccount);
        		firstLock = redisson.getLock("account-lock:" + lockAccount1);
    	        secondLock = redisson.getLock("account-lock:" + lockAccount2);
                locked = redisson.getMultiLock(firstLock, secondLock).tryLock(10, 30, TimeUnit.SECONDS);
            } else {
            	firstLock = redisson.getLock("account-lock:" + fromAccount);
                locked = firstLock.tryLock(10, 30, TimeUnit.SECONDS);
            }

        	
        	if (!locked) {
        		throw new CustomException(HttpServletResponse.SC_CONFLICT, "Transaction failed due to concurrent access.");
        	}
        	Map<String, Object> fromAccountMap = new AccountFunctions().get(fromAccount);
        	Map<String, Object> toAccountMap=null;
        	
        	if(toAccount!=null) {
        		toAccountMap = new AccountFunctions().get(toAccount);
        	}
        	if(Validator.isNull(fromAccountMap)) {
        		throw new CustomException(HttpServletResponse.SC_NOT_FOUND, "Sender account not found.");
        	}
        	if(role.equals(Role.CUSTOMER))
        	{
        		if(userId != Converter.convertToLong(fromAccountMap.get("userId"))) {
        			throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Permission denied to access this account.");
        		}
        	} else if(role.equals(Role.EMPLOYEE)) {
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
        	if(amount>9999999)
        	{
        		throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Amount more than Rs. 99,99,999 cannot be sent.");
        	}
        	float decimalPart = amount - amount.intValue();
        	if(decimalPart!=0&&decimalPart<0.01)
        	{
        		throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Cannot send amount less than 0.01 rupees.");
        	}
        	if(!Validator.decimalChecker(amount))
        	{
        		System.out.println(amount);
        		throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Can have only 2 digits after the decimal.");
        	}
        	if((transactionType.equals("same-bank")||transactionType.equals("other-bank"))&&Validator.isNull(toAccount))
        	{				
        		throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "Reciever account is required.");
        	}
        	
        	fromAccountUserId = Converter.convertToLong(fromAccountMap.get("userId"));
        	if(transactionType.equals("same-bank")) {
        		toAccountUserId = Converter.convertToLong(toAccountMap.get("userId"));
        	}
        	new TransactionFunctions().initiateTransaction(details, fromAccountMap, toAccountMap);	
        } finally {
        	if (firstLock.isHeldByCurrentThread()) {
        		firstLock.unlock();
        	}
        	if (secondLock!=null && secondLock.isHeldByCurrentThread()) {
        		secondLock.unlock();
        	}
        }
        	
    	new Activity()
    	.setAction("TRANSACTION")
    	.setRecordname("transaction")
    	.setActorId(userId)
    	.setSubjectId(fromAccountUserId)
    	.setKeyValue(fromAccount)
    	.setDetails(jsonObject.toString())
    	.setActionTime(System.currentTimeMillis())
    	.execute();
    	
    	if(transactionType.equals("same-bank")) {
    		new Activity()
    		.setAction("TRANSACTION")
    		.setRecordname("transaction")
    		.setActorId(userId)
    		.setSubjectId(toAccountUserId)
    		.setKeyValue(toAccount)
    		.setDetails(jsonObject.toString())
    		.setActionTime(System.currentTimeMillis())
    		.execute();
    	}
    	
    	AsyncLoggerUtil.log(TransactionHandler.class, Level.INFO, "Transaction success.");
        Writer.responseMapWriter(response, 
        		HttpServletResponse.SC_OK, 
        		HttpServletResponse.SC_OK, 
        		"Transaction success.", 
        		responseMap);
	}
}
