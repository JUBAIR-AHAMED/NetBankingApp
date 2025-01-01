package com.netbanking.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import com.netbanking.util.Encryption;
import com.netbanking.util.Parser;
import com.netbanking.util.Redis;
import com.netbanking.util.ServletHelper;
import com.netbanking.util.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiHandler {
	// redis ok
	public Map<String, Object> loginHandler(HttpServletRequest request) throws IOException, CustomException, Exception
	{
		try(BufferedReader reader = request.getReader())
		{
			JsonObject jsonObject = Parser.getJsonObject(reader);
			Long userId = jsonObject.get("username").getAsLong();
			String password = jsonObject.get("password").getAsString();
			Map<String, Object> map = get(userId, User.class);
			System.out.println("map: "+map);
			if(map != null && !map.isEmpty()) {
				Boolean check=Encryption.verifyPassword(password, (String) map.get("password"));
				if(!check) {
					throw new CustomException("Wrong Password");
				}
			} else {
				throw new CustomException("Invalid user / customer id.");
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
			if(filters!=null && !filters.isEmpty()) {
				throw new Exception("Filter fields is not allowed for the customer");
			}
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
				
		Map<String, Object> fromAccountMap = get(fromAccount, Account.class);
		Map<String, Object> toAccountMap=null;
		if(toAccount!=null) {
			toAccountMap = get(toAccount, Account.class);
		}
		
		if(fromAccountMap == null) {
			throw new CustomException("Invalid accounts.");
		}
		if(role.equals("CUSTOMER"))
		{
			if(userId != (Long) fromAccountMap.get("userId")) {
				throw new CustomException("You don't have permission to access this account.");
			}
		} else if(role.equals("EMPLOYEE")) {
			if(branchId != (Long) fromAccountMap.get("branchId")) {
				throw new CustomException("You don't have permission to access this account.");
			}
		}
		
		String fromAccountStatus = (String) fromAccountMap.get("status");
		String toAccountStatus = toAccountMap!=null? (String) toAccountMap.get("status"):null;
		
		if(!fromAccountStatus.equals("ACTIVE"))
		{
			throw new CustomException("Sender Account is "+ fromAccountStatus);
		}
		if(toAccountStatus!=null&&!toAccountStatus.equals("ACTIVE"))
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
		String cacheKeyFromAcc = "ACCOUNT$ACCOUNT_NUMBER:"+fromAccount;
		String cacheKeyToAcc = "ACCOUNT$ACCOUNT_NUMBER:"+toAccount;
		String cacheKeyFromAccTran = "TRANSACTION$ACCOUNT_NUMBER:"+fromAccount;
		String cacheKeyToAccTran = "TRANSACTION$ACCOUNT_NUMBER:"+toAccount;
		if((transactionType.equals("same-bank")||transactionType.equals("other-bank"))&&toAccount==null)
		{				
			throw new CustomException("Reciever account is required.");
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
                
	    Integer currentPage = jsonObject.has("currentPage") && !jsonObject.get("currentPage").isJsonNull() 
	            ? jsonObject.get("currentPage").getAsInt() 
	            : null;

        Boolean count = jsonObject.has("count") && !jsonObject.get("count").isJsonNull() 
	            ? jsonObject.get("count").getAsBoolean() 
	            : null;
	    
        Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
	            
		FunctionHandler functionHandler = new FunctionHandler();
		
		Map<String, Object> accountMap = get(accountNumber, Account.class);
		
		if(accountMap == null) {
			throw new CustomException("Invalid account.");
		}
		
		if(role.equals("CUSTOMER"))
		{
			if(userId != ServletHelper.convertToLong(accountMap.get("userId"))) {
				throw new CustomException("You don't have permission to access this account.");
			}
		} else if(role.equals("EMPLOYEE")) {
			if(branchId != (Long) accountMap.get("branchId")) {
				throw new CustomException("You don't have permission to access this account.");
			}
		}
		
		String accountStatus = (String) accountMap.get("status");
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
	@SuppressWarnings("unchecked")
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
		ObjectMapper objectMapper = new ObjectMapper();
		FunctionHandler functionHandler = new FunctionHandler();
		String cachedData = Redis.get(cacheKey);
//		if(cachedData!=null) {
//			return objectMapper.readValue(cachedData, Map.class);
//		} 
		Map<String, Object> map = functionHandler.getRecord(userId, clazz);
		Redis.setex(cacheKey, map);
		return map;
	}
	
	// no redis
	public long createEmployee(HttpServletRequest request, Long userId, String role, Long branchId) throws Exception {
		StringBuilder sb = ApiHelper.getJsonBody(request);
		System.out.println("employee "+sb.toString());
		Employee employee = ApiHelper.getPojoFromRequest(sb, Employee.class);
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
		Branch branch = ApiHelper.getPojoFromRequest(ApiHelper.getJsonBody(request), Branch.class);
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
		Account account = ApiHelper.getPojoFromRequest(ApiHelper.getJsonBody(request), Account.class);
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
		Customer customer = ApiHelper.getPojoFromRequest(ApiHelper.getJsonBody(request), Customer.class);
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		customer.setRole(Role.CUSTOMER);
		customer.setStatus(Status.ACTIVE);
		FunctionHandler functionHandler = new FunctionHandler();
		Redis.deleteKeysWithStartString("ACCOUNT");
		return functionHandler.create(customer);
	}
	
	// redis ok
	public void updateCustomer(StringBuilder jsonBody, Long userId) throws Exception {
		Customer customer = ApiHelper.getPojoFromRequest(jsonBody, Customer.class);
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
		functionHandler.update(customer, Customer.class, userId);
	}
	
	// redis ok
	public void updateEmployee(StringBuilder jsonBody, Long userId) throws Exception {
		Employee employee = ApiHelper.getPojoFromRequest(jsonBody, Employee.class);
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
		functionHandler.update(employee, Employee.class, userId);
	}
	
	// redis ok
	public void updateUser(StringBuilder jsonBody, Long userId) throws Exception {
		User user = ApiHelper.getPojoFromRequest(jsonBody, User.class);
		if(user==null) {
			return;
		}
		String cacheKey = "USER$USER_ID:"+userId;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		user.setCreationTime(System.currentTimeMillis());
		user.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(user, User.class, userId);
	}
	
	// redis and impl needed
	public void updateAccount(StringBuilder jsonBody, Long userId) {
	}
}