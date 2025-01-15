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
import com.netbanking.enums.Role;
import com.netbanking.enums.Status;
import com.netbanking.exception.CustomException;
import com.netbanking.model.Model;
import com.netbanking.object.Account;
import com.netbanking.object.Branch;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
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

			Long userId = Parser.getValue(jsonObject, "userId", Long.class, "User Id", true);
			String password = Parser.getValue(jsonObject, "password", String.class, "Password", true);
			Map<String, Object> map = get(userId, User.class);
			
			if(map != null && !map.isEmpty()) {
				Boolean check=Encryption.verifyPassword(password, (String) map.get("password"));
				if(!check) {
					throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid password.");
				}
			} else {
				throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid customer id.");
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
		
		StringBuilder cacheKeyBuilder = new StringBuilder("ACCOUNT");
		String cacheKeyForRetrieval = cacheKeyBuilder.append("$COUNT:").append(filters.getOrDefault("count", null))
				.append("$ACCOUNT_NUMBER:").append(filters.getOrDefault("accountNumber", null))
				.append("$USER_ID:").append(filters.getOrDefault("userId", null))
				.append("$BRANCH_ID:").append(filters.getOrDefault("branchId", null))
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
		StringBuilder cacheKeyBuilder = new StringBuilder("BRANCH");
		String cacheKeyForRetrieval = null;
		cacheKeyForRetrieval = cacheKeyBuilder.append("$COUNT:").append(filters.getOrDefault("count", null))
												.append("$BRANCH_ID:").append(filters.getOrDefault("branchId", null))
												.append("$EMPLOYEE_ID:").append(filters.getOrDefault("employeeId", null))
												.append("$IFSC:").append(filters.getOrDefault("ifsc", null))
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
		StringBuilder cacheKeyBuilder = new StringBuilder(clazz.getSimpleName().toUpperCase());
		String cacheKeyForRetrieval = null;
		cacheKeyForRetrieval = cacheKeyBuilder.append("$COUNT:").append(filters.getOrDefault("count", null))
												.append("$NAME:").append(filters.getOrDefault("name", null))
												.append("$USER_ID:").append(filters.getOrDefault("userId", null))
												.append("$EMAIL:").append(filters.getOrDefault("email", null))
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
		}
		List<Map<String, Object>> list = functionHandler.getUser(filters, limit, offset, clazz);
		for(Map<String, Object> map:list) {
			// As count is also maintained in this function
			if(filters.containsKey("count")) {
				Redis.setex(cacheKeyForRetrieval, map);
			}
			Long id = (Long) map.get("userId");
			String cacheKey = "USER$USER_ID:";
			cacheKey += id;
			if(Redis.exists(cacheKey)) {
				continue;
			}
			Redis.setex(cacheKey, map);
		}
		return list;
	}
	
	// redis ok
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
	
	// redis ok
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
	
	// no redis
	public long createEmployee(Employee employee, Long userId) throws Exception {
		employee.setPassword(Encryption.hashPassword(employee.getPassword()));
		employee.setStatus(Status.ACTIVE);
		employee.setCreationTime(System.currentTimeMillis());
		employee.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    Redis.deleteKeysWithStartString("EMPLOYEE$COUNT");
    	return functionHandler.create(employee);
	}
	
	// no redis
	public long createBranch(Branch branch, Long userId) throws CustomException, Exception{
		branch.setCreationTime(System.currentTimeMillis());
	    branch.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    Redis.deleteKeysWithStartString("BRANCH$COUNT");
    	return functionHandler.create(branch);
	}
	
	// no redis
	public long createAccount(Account account, Long userId) throws CustomException, Exception {
		account.setDateOfOpening(System.currentTimeMillis());
	    account.setCreationTime(System.currentTimeMillis());
	    account.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    Redis.deleteKeysWithStartString("ACCOUNT$COUNT");
		return functionHandler.create(account);
	}
	
	// no redis
	public long createCustomer(Customer customer, Long userId) throws Exception {
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		customer.setRole(Role.CUSTOMER);
		customer.setStatus(Status.ACTIVE);
		FunctionHandler functionHandler = new FunctionHandler();
		Redis.deleteKeysWithStartString("CUSTOMER$COUNT");
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
		Redis.deleteKeysWithStartString("CUSTOMER$COUNT");
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
		Redis.deleteKeysWithStartString("EMPLOYEE$COUNT");
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
		Redis.deleteKeysWithStartString("USER$COUNT");
		user.setModifiedTime(System.currentTimeMillis());
		user.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(user, User.class, key);
	}
	
	// redis and impl needed
	public void updateAccount(Map<String, Object> data, Long userId, Long key) throws Exception {
		Account account = ApiHelper.getPojoFromRequest(data, Account.class);
		if(account==null) {
			return;
		}
		String cacheKey = "ACCOUNT$ACCOUNT_NUMBER:"+key;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		Redis.deleteKeysWithStartString("ACCOUNT$COUNT");
		account.setCreationTime(System.currentTimeMillis());
		account.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(account, Account.class, key);
	}
}