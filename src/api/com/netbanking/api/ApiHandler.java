package com.netbanking.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.netbanking.object.Role;
import com.netbanking.object.Status;
import com.netbanking.object.User;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Converter;
import com.netbanking.util.Encryption;
import com.netbanking.util.Parser;
import com.netbanking.util.Redis;
import com.netbanking.util.Validator;

public class ApiHandler {
	// redis ok
	public Map<String, Object> loginHandler(HttpServletRequest request) throws IOException, CustomException, Exception
	{
		try(BufferedReader reader = request.getReader())
		{
			JsonObject jsonObject = Parser.getJsonObject(reader);

			Validator.checkInvalidInput(jsonObject.get("userId"), "User Id");
			Long userId = jsonObject.get("userId").getAsLong();
			Validator.checkInvalidInput(jsonObject.get("password"), "Password");
			String password = jsonObject.get("password").getAsString();
			
			Map<String, Object> map = get(userId, User.class);			
			if(map != null && !map.isEmpty()) {
				Boolean check=Encryption.verifyPassword(password, (String) map.get("password"));
				if(!check) {
					throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid password.");
				}
			} else {
				throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user / customer id.");
			}
			String role = (String) map.get("role");
			Long user_id;
			if(map.get("userId").getClass().getSimpleName().equals("Integer")) {
				user_id = ((Integer) map.get("userId")).longValue();
			} else {
				user_id = (Long) map.get("userId");
			}
			if(role.equals("EMPLOYEE")||role.equals("MANAGER")) {
				map.putAll(get(user_id, Employee.class));
			}
			else if(role.equals("CUSTOMER"))
			{
				map.putAll(get(user_id, Customer.class));
			}
			return map;
		}
	}
	
	// redis ok
	public List<Map<String, Object>> filteredGetAccount(Long userId, String role, Long branchId, Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		FunctionHandler functionHandler = new FunctionHandler();
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
		
		if(role.equals("CUSTOMER")) {
			filters = new HashMap<>();
			filters.put("userId", userId);
			filters.put("isInActiveRequired", false);
		} else {
			filters.put("isInActiveRequired", true);
		}
		
		StringBuilder cacheKeyBuilder = new StringBuilder("ACCOUNT$ACCOUNT_NUMBER:"+filters.get("accountNumber"));
		
		String cacheKeyForRetrieval = null;
		cacheKeyForRetrieval = cacheKeyBuilder.append("$USER_ID:").append(filters.getOrDefault("userId", null))
				.append("$BRANCH_ID:").append(filters.getOrDefault("branchId", null))
				.append("$COUNT:").append(filters.getOrDefault("count", null))
				.toString();
		if(filters.containsKey("count")) {
			if(Redis.exists(cacheKeyForRetrieval)) {
				ObjectMapper objectMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> map = objectMapper.readValue(Redis.get(cacheKeyForRetrieval), Map.class);
				List<Map<String, Object>> list = new ArrayList<>();
				list.add(map);
				return list;
			}
		} else if (filters.containsKey("accountNumber")&&filters.size()==3) {
				String cacheKey = "ACCOUNT$ACCOUNT_NUMBER:" + filters.get("accountNumber");
				if(Redis.exists(cacheKey)) {
					ObjectMapper objectMapper = new ObjectMapper();
					@SuppressWarnings("unchecked")
					Map<String, Object> map = objectMapper.readValue(Redis.get(cacheKey), Map.class);
					List<Map<String, Object>> list = new ArrayList<>();
					list.add(map);
					return list;
				}
		}
		List<Map<String, Object>> list = functionHandler.getAccount(filters, limit, offset);
		for(Map<String, Object> map:list) {
			// As count is also maintained in this function
			if(filters.containsKey("count")) {
				Redis.setex(cacheKeyForRetrieval, map);
			}
			Long id = (Long) map.get("accountNumber");
			String cacheKey = "ACCOUNT$ACCOUNT_NUMBER:";
			cacheKey += id;
			if(Redis.exists(cacheKey)) {
				continue;
			}
			Redis.setex(cacheKey, map);
		}
		return list;
	}
	
	public List<Map<String, Object>> filteredGetBranch(Long userId, String role, Long branchId, Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		FunctionHandler functionHandler = new FunctionHandler();
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
		StringBuilder cacheKeyBuilder = new StringBuilder("BRANCH$BRANCH_ID:"+filters.get("branchId"));
		String cacheKeyForRetrieval = null;
		cacheKeyForRetrieval = cacheKeyBuilder.append("$EMPLOYEE_ID:").append(filters.getOrDefault("employeeId", null))
				.append("$BRANCH_ID:").append(filters.getOrDefault("branchId", null))
				.append("$COUNT:").append(filters.getOrDefault("count", null))
				.toString();
		if(filters.containsKey("count")) {
			if(Redis.exists(cacheKeyForRetrieval)) {
				ObjectMapper objectMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> map = objectMapper.readValue(Redis.get(cacheKeyForRetrieval), Map.class);
				List<Map<String, Object>> list = new ArrayList<>();
				list.add(map);
				return list;
			}
		} else if (filters.containsKey("branchId")&&filters.size()==1) {
				String cacheKey = "BRANCH$BRANCH_ID:" + filters.get("branchId");
				if(Redis.exists(cacheKey)) {
					ObjectMapper objectMapper = new ObjectMapper();
					@SuppressWarnings("unchecked")
					Map<String, Object> map = objectMapper.readValue(Redis.get(cacheKey), Map.class);
					List<Map<String, Object>> list = new ArrayList<>();
					list.add(map);
					return list;
				}
		}
		List<Map<String, Object>> list = functionHandler.getBranch(filters, limit, offset);
		for(Map<String, Object> map:list) {
			// As count is also maintained in this function
			if(filters.containsKey("count")) {
				Redis.setex(cacheKeyForRetrieval, map);
			}
			Long id = (Long) map.get("branchId");
			String cacheKey = "BRANCH$BRANCH_ID:";
			cacheKey += id;
			if(Redis.exists(cacheKey)) {
				continue;
			}
			Redis.setex(cacheKey, map);
		}
		return list;
	}
	
	public List<Map<String, Object>> filteredGetCustomerOrEmployee(Long userId, String role, Long branchId, Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		FunctionHandler functionHandler = new FunctionHandler();
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
		String userType = (String) filters.remove("userType");
		Class<? extends Model> clazz = null;
		if(userType.equals("employee")) {
			clazz = Employee.class;
		} else if(userType.equals("customer")) {
			clazz = Customer.class;
		}
//		StringBuilder cacheKeyBuilder = new StringBuilder("ACCOUNT$ACCOUNT_NUMBER:"+filters.get("accountNumber"));
//		String cacheKeyForRetrieval = null;
//		cacheKeyForRetrieval = cacheKeyBuilder.append("$USER_ID:").append(filters.getOrDefault("userId", null))
//				.append("$BRANCH_ID:").append(filters.getOrDefault("branchId", null))
//				.append("$COUNT:").append(filters.getOrDefault("count", null))
//				.toString();
//		if(filters.containsKey("count")) {
//			if(Redis.exists(cacheKeyForRetrieval)) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				@SuppressWarnings("unchecked")
//				Map<String, Object> map = objectMapper.readValue(Redis.get(cacheKeyForRetrieval), Map.class);
//				List<Map<String, Object>> list = new ArrayList<>();
//				list.add(map);
//				return list;
//			}
//		} else if (filters.containsKey("accountNumber")&&filters.size()==3) {
//				String cacheKey = "ACCOUNT$ACCOUNT_NUMBER:" + filters.get("accountNumber");
//				if(Redis.exists(cacheKey)) {
//					ObjectMapper objectMapper = new ObjectMapper();
//					@SuppressWarnings("unchecked")
//					Map<String, Object> map = objectMapper.readValue(Redis.get(cacheKey), Map.class);
//					List<Map<String, Object>> list = new ArrayList<>();
//					list.add(map);
//					return list;
//				}
//		}
		List<Map<String, Object>> list = functionHandler.getUser(filters, limit, offset, clazz);
//		for(Map<String, Object> map:list) {
//			// As count is also maintained in this function
//			if(filters.containsKey("count")) {
//				Redis.setex(cacheKeyForRetrieval, map);
//			}
//			Long id = (Long) map.get("accountNumber");
//			String cacheKey = "ACCOUNT$ACCOUNT_NUMBER:";
//			cacheKey += id;
//			if(Redis.exists(cacheKey)) {
//				continue;
//			}
//			Redis.setex(cacheKey, map);
//		}
		return list;
	}
	
	// redis ok
	public void initiateTransaction(HttpServletRequest request, Long userId, String role, Long branchId) throws CustomException, Exception {
		JsonObject jsonObject = Parser.getJsonObject(request);
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
			throw new CustomException(1, "Enter numeric values for account number and amount.");
		}
		FunctionHandler functionHandler = new FunctionHandler();
				
		Map<String, Object> fromAccountMap = get(fromAccount, Account.class);
		Map<String, Object> toAccountMap=null;
		if(toAccount!=null) {
			toAccountMap = get(toAccount, Account.class);
		}
		
		if(fromAccountMap == null) {
			throw new CustomException(1, "Invalid accounts.");
		}
		if(role.equals("CUSTOMER"))
		{
			if(userId != (Long) fromAccountMap.get("userId")) {
				throw new CustomException(1, "You don't have permission to access this account.");
			}
		} else if(role.equals("EMPLOYEE")) {
			if(branchId != (Long) fromAccountMap.get("branchId")) {
				throw new CustomException(1, "You don't have permission to access this account.");
			}
		}
		
		String fromAccountStatus = (String) fromAccountMap.get("status");
		String toAccountStatus = toAccountMap!=null? (String) toAccountMap.get("status"):null;
		
		if(!fromAccountStatus.equals("ACTIVE"))
		{
			throw new CustomException(1, "Sender Account is "+ fromAccountStatus);
		}
		if(toAccountStatus!=null&&!toAccountStatus.equals("ACTIVE"))
		{
			throw new CustomException(1, "Reciever Account is "+ toAccountStatus);
		}
		
		if(fromAccount.equals(toAccount))
		{
			throw new CustomException(1, "Cannot send money to the same account.");
		}
		if(amount<0)
		{
			throw new CustomException(1, "Amount cannot be negative.");
		}
		float decimalPart = amount - amount.intValue();
		if(decimalPart!=0&&decimalPart<0.01)
		{
			throw new CustomException(1, "Cannot send amount less than 0.01 rupees.");
		}
		if(!Validator.decimalChecker(amount))
		{
			throw new CustomException(1, "Can have only 2 digits after the decimal.");
		}
		String cacheKeyFromAcc = "ACCOUNT$ACCOUNT_NUMBER:"+fromAccount;
		String cacheKeyToAcc = "ACCOUNT$ACCOUNT_NUMBER:"+toAccount;
		String cacheKeyFromAccTran = "TRANSACTION$ACCOUNT_NUMBER:"+fromAccount;
		String cacheKeyToAccTran = "TRANSACTION$ACCOUNT_NUMBER:"+toAccount;
		if((transactionType.equals("same-bank")||transactionType.equals("other-bank"))&&toAccount==null)
		{				
			throw new CustomException(1, "Reciever account is required.");
		}
		functionHandler.makeTransaction(fromAccountMap, toAccountMap, userId, amount, transactionType);
		Redis.delete(cacheKeyFromAcc);
		Redis.delete(cacheKeyToAcc);
		Redis.deleteKeysWithStartString(cacheKeyFromAccTran);
		if(cacheKeyToAccTran!=null) {
			Redis.deleteKeysWithStartString(cacheKeyToAccTran);
		}
	}
	
	// redis ok
	@SuppressWarnings("unchecked")
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
			throw new CustomException(1,"Invalid account.");
		}
		
		if(role.equals("CUSTOMER"))
		{
			if(userId != Converter.convertToLong(accountMap.get("userId"))) {
				throw new CustomException(1, "You don't have permission to access this account.");
			}
		} else if(role.equals("EMPLOYEE")) {
			if(branchId != (Long) accountMap.get("branchId")) {
				throw new CustomException(1, "You don't have permission to access this account.");
			}
		}
		
		String accountStatus = (String) accountMap.get("status");
		if(accountStatus.equals("BLOCKED")||accountStatus.equals("INACTIVE"))
		{
			throw new CustomException(1,"Account is "+accountStatus+".");
		}
		
		if(fromDate==null&&toDate==null&&limit==null)
		{
			throw new CustomException(1, "Please enter valid details");
		}
		if((fromDate==null&&toDate!=null)||(fromDate!=null&&toDate==null)) {
			throw new CustomException(1, "Time frame is invalid.");
		}
		if(fromDate!=null && fromDate>toDate)
		{
			throw new CustomException(1, "Time frame is invalid.");
		}
		
		try {
			StringBuilder sb = new StringBuilder("TRANSACTION");
		    sb.append("$ACCOUNT_NUMBER:").append(accountNumber)
		    .append("$FROM_DATE:").append(fromDate)
		    .append("$TO_DATE:").append(toDate)
		    .append("$LIMIT:").append(limit)
		    .append("$CURRENT_PAGE:").append(currentPage)
		    .append("$COUNT:").append(count);
		    String cacheKey = sb.toString();
		    if(Redis.exists(cacheKey)) {
		    	String cachedDate = Redis.get(cacheKey);
		    	ObjectMapper objectMapper = new ObjectMapper();
		    	return objectMapper.readValue(cachedDate, List.class);
		    }
			List<Map<String, Object>> list = functionHandler.getTransactionStatement(accountNumber, fromDate, toDate, limit, offset, count);
			Redis.setex(cacheKey, list);
			return list;
		} catch (CustomException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// redis ok
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
//		ObjectMapper objectMapper = new ObjectMapper();
//		String cachedData = Redis.get(cacheKey);
//		if(cachedData!=null) {
//			return objectMapper.readValue(cachedData, Map.class);
//		} 
		Map<String, Object> map = functionHandler.getRecord(userId, clazz);
		Redis.setex(cacheKey, map);
		return map;
	}
	
	// no redis
	public long createEmployee(HttpServletRequest request, Long userId, String role, Long branchId) throws Exception {
		StringBuilder jsonBody = Parser.getJsonBody(request);
		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
		Employee employee = ApiHelper.getPojoFromRequest(data, Employee.class);
		employee.setPassword(Encryption.hashPassword(employee.getPassword()));
		employee.setStatus(Status.ACTIVE);
		employee.setCreationTime(System.currentTimeMillis());
		employee.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    Redis.deleteKeysWithStartString("EMPLOYEE");
	    try {	    	
	    	return functionHandler.create(employee);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new Exception("Failed Creating employee.");
		}
	}
	
	// no redis
	public long createBranch(HttpServletRequest request, Long userId, String role, Long branchId) throws Exception {
		StringBuilder jsonBody = Parser.getJsonBody(request);
		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
		Branch branch = ApiHelper.getPojoFromRequest(data, Branch.class);
		branch.setCreationTime(System.currentTimeMillis());
	    branch.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    Redis.deleteKeysWithStartString("BRANCH");
	    try {
	    	return functionHandler.create(branch);
	    } catch (Exception e) {
	    	throw new Exception("Failed creating branch.");
	    }
	}
	
	// no redis
	public long createAccount(HttpServletRequest request, Long userId) throws Exception {
		StringBuilder jsonBody = Parser.getJsonBody(request);
		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
		Account account = ApiHelper.getPojoFromRequest(data, Account.class);
		account.setDateOfOpening(System.currentTimeMillis());
	    account.setCreationTime(System.currentTimeMillis());
	    account.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    Redis.deleteKeysWithStartString("ACCOUNT");
	    try {
			return functionHandler.create(account);
		} catch (Exception e) {
			throw new Exception("Failed to create account");
		}
	}
	
	// no redis
	public long createCustomer(HttpServletRequest request, Long userId) throws Exception {
		StringBuilder jsonBody = Parser.getJsonBody(request);
		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
		Customer customer = ApiHelper.getPojoFromRequest(data, Customer.class);
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		customer.setRole(Role.CUSTOMER);
		customer.setStatus(Status.ACTIVE);
		FunctionHandler functionHandler = new FunctionHandler();
		Redis.deleteKeysWithStartString("ACCOUNT");
		return functionHandler.create(customer);
	}
	
	// redis ok
	public void updateCustomer(Customer customer, Long userId, Long key) throws Exception {
		if(customer==null) {
			return;
		}
		String cacheKey = "CUSTOMER$USER_ID:"+userId;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(customer, Customer.class, key);
	}
	
	// redis ok
	public void updateEmployee(Employee employee, Long userId, Long key) throws Exception {
		if(employee==null) {
			return;
		}
		String cacheKey = "EMPLOYEE$USER_ID:"+userId;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		employee.setCreationTime(System.currentTimeMillis());
		employee.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(employee, Employee.class, key);
	}
	
	// redis ok
	public void updateUser(User user, Long userId, Long key) throws Exception {
		if(user==null) {
			return;
		}
		String cacheKey = "USER$USER_ID:"+userId;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		user.setModifiedTime(System.currentTimeMillis());
		user.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(user, User.class, key);
	}
	
	// redis and impl needed
	public void updateAccount(StringBuilder jsonBody, Long userId) throws Exception {
		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
		Account account = ApiHelper.getPojoFromRequest(data, Account.class);
		if(account==null) {
			return;
		}
//		String cacheKey = "USER$USER_ID:"+userId;
//		if(Redis.exists(cacheKey)) {
//			Redis.delete(cacheKey);
//		}
		account.setCreationTime(System.currentTimeMillis());
		account.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		Long accountNumber = account.getAccountNumber();
		account.setAccountNumber(null);
		functionHandler.update(account, Account.class, accountNumber);
	}
}