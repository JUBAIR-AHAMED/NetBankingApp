package com.netbanking.api;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.exception.CustomException;
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
import com.netbanking.util.Validator;

public class ApiHandler {
	public Map<String, Object> loginHandler(HttpServletRequest request) throws IOException, CustomException, Exception
	{
		try(BufferedReader reader = request.getReader())
		{
			JsonObject jsonObject = Parser.getJsonObject(reader);
			Long userId = jsonObject.get("username").getAsLong();
			String password = jsonObject.get("password").getAsString();
			FunctionHandler functionHandler = new FunctionHandler();
			Map<String, Object> map = functionHandler.getRecord(userId, User.class);
			if(map != null && !map.isEmpty()) {
				Boolean check=Encryption.verifyPassword(password, (String) map.get("password"));
				if(!check) {
					throw new CustomException("Wrong Password");
				}
			} else {
				throw new CustomException("Invalid user / customer id.");
			}
			String role = (String) map.get("role");
			Long user_id = (Long) map.get("userId");
			FunctionHandler functionalHandler = new FunctionHandler();
			if(role.equals("EMPLOYEE")||role.equals("MANAGER")) {
				map.putAll(functionalHandler.getRecord(user_id, Employee.class));
			}
			else if(role.equals("CUSTOMER"))
			{
				map.putAll(functionalHandler.getRecord(user_id, Customer.class));
			}
			return map;
		}
	}
	
	public List<Map<String, Object>> getUserAccounts(Long userId, String role, Long branchId, List<String> filterFields, List<Object> filterValues, Integer limit, Integer currentPage) throws Exception
	{
		
		FunctionHandler functionHandler = new FunctionHandler();
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
		if(role.equals("CUSTOMER")) {
			if(filterFields!=null && !filterFields.isEmpty()) {
				throw new Exception("Filter fields is not allowed for the customer");
			}
			filterFields = new ArrayList<String>();
			filterFields.add("userId");
			filterValues.add(userId);
//			return functionHandler.getAccounts(filterFields, filterValues, false, limit, offset);
		}
		return functionHandler.getAccounts(filterFields, filterValues, true, limit, offset);
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
				
		Map<String, Object> fromAccountMap = functionHandler.getRecord(fromAccount, Account.class);
		Map<String, Object> toAccountMap = functionHandler.getRecord(toAccount, Account.class);
		
		if(fromAccountMap == null || toAccountMap == null) {
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
		String toAccountStatus = (String) toAccountMap.get("status");
		
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
		
		Map<String, Object> accountMap = functionHandler.getRecord(accountNumber, Account.class);
		
		if(accountMap == null) {
			throw new CustomException("Invalid account.");
		}
		
		if(role.equals("CUSTOMER"))
		{
			if(userId != (Long) accountMap.get("userId")) {
				throw new CustomException("You don't have permission to access this account.");
			}
		} else if(role.equals("EMPLOYEE")) {
			if(branchId != (Long) accountMap.get("branchId")) {
				throw new CustomException("You don't have permission to access this account.");
			}
		}
		
		String accountStatus =(String) functionHandler.getRecord(accountNumber, Account.class).get("status");
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
			return functionHandler.getTransactionStatement(accountNumber, fromDate, toDate, limit, offset, count);
		} catch (CustomException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Map<String, Object> getProfile(Long userId, String role) throws CustomException, Exception
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
		
		Map<String, Object> accountMap = functionHandler.getRecord(accountNumber, Account.class);
		
		if(accountMap == null) {
			throw new CustomException("Invalid account.");
		}
		
		if(role.equals("CUSTOMER"))
		{
			if(userId != (Long) accountMap.get("userId")) {
				throw new CustomException("You don't have permission to access this account.");
			}
		} else if(role.equals("EMPLOYEE")) {
			if(branchId != (Long) accountMap.get("branchId")) {
				throw new CustomException("You don't have permission to access this account.");
			}
		}
		
		if(!actionType.equals("BLOCK")&&!actionType.equals("UNBLOCK")&&!actionType.equals("DELETE")) {
			throw new CustomException("Invalid action type.");
		}
		
        if(accountMap.get("status").equals("INACTIVE")) {
        	throw new CustomException("Cannot perform any actions this account is inactive.");
        }
		
//		functionHandler.actionHandler(actionType, "ACCOUNT", accountNumber);
	}
	
	public long createEmployee(HttpServletRequest request, Long userId, String role, Long branchId) throws Exception {
		Employee employee = ApiHelper.getPojoFromRequest(ApiHelper.getJsonBody(request), Employee.class);
		employee.setStatus(Status.ACTIVE);
		employee.setCreationTime(System.currentTimeMillis());
		employee.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    try {	    	
	    	return functionHandler.create(employee);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new Exception("Failed Creating employee.");
		}
	}
	
	public long createBranch(HttpServletRequest request, Long userId, String role, Long branchId) throws Exception {
		Branch branch = ApiHelper.getPojoFromRequest(ApiHelper.getJsonBody(request), Branch.class);
		branch.setCreationTime(System.currentTimeMillis());
	    branch.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    try {
	    	return functionHandler.create(branch);
	    } catch (Exception e) {
	    	throw new Exception("Failed creating branch.");
	    }
	}
	
	public long createAccount(HttpServletRequest request, Long userId) throws Exception {
		Account account = ApiHelper.getPojoFromRequest(ApiHelper.getJsonBody(request), Account.class);
		account.setDateOfOpening(System.currentTimeMillis());
	    account.setCreationTime(System.currentTimeMillis());
	    account.setModifiedBy(userId);
	    FunctionHandler functionHandler = new FunctionHandler();
	    try {
			return functionHandler.create(account);
		} catch (Exception e) {
			throw new Exception("Failed to create account");
		}
	}
	
	public long createCustomer(HttpServletRequest request, Long userId) throws Exception {
		Customer customer = ApiHelper.getPojoFromRequest(ApiHelper.getJsonBody(request), Customer.class);
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		customer.setRole(Role.CUSTOMER);
		customer.setStatus(Status.ACTIVE);
		FunctionHandler functionHandler = new FunctionHandler();
		return functionHandler.create(customer);
	}
	
	public void updateCustomer(StringBuilder jsonBody, Long userId) throws Exception {
		Customer customer = ApiHelper.getPojoFromRequest(jsonBody, Customer.class);
		if(customer==null) {
			return;
		}
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(customer, Customer.class, userId);
	}
	
	public void updateEmployee(StringBuilder jsonBody, Long userId) throws Exception {
		Employee employee = ApiHelper.getPojoFromRequest(jsonBody, Employee.class);
		if(employee==null) {
			return;
		}
		employee.setCreationTime(System.currentTimeMillis());
		employee.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(employee, Employee.class, userId);
	}
	
	public void updateUser(StringBuilder jsonBody, Long userId) throws Exception {
		User user = ApiHelper.getPojoFromRequest(jsonBody, User.class);
		System.out.println(user);
		if(user==null) {
			return;
		}
		user.setCreationTime(System.currentTimeMillis());
		user.setModifiedBy(userId);
		FunctionHandler functionHandler = new FunctionHandler();
		functionHandler.update(user, User.class, userId);
	}
}