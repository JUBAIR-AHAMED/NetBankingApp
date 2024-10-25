package com.netbanking.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netbanking.exception.CustomException;
import com.netbanking.object.Account;
import com.netbanking.object.Branch;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.object.QueryRequest;
import com.netbanking.object.Role;
import com.netbanking.object.Status;
import com.netbanking.object.Transaction;
import com.netbanking.object.User;
import com.netbanking.util.Encryption;
import com.netbanking.util.Validator;

public class FunctionHandler {
	public Map<String, Object> getLogin(Long user_id, String password) throws CustomException {
		System.out.println(user_id+" "+password);
		Validator.checkInvalidInput(user_id, password);
		DaoHandler<User> daoCaller = new DaoHandler<User>();
	
		List<String> whereCondition = new ArrayList<>();
		whereCondition.add("user_id");
		
		List<Object> whereConditionValues = new ArrayList<>();
		whereConditionValues.add(user_id);
		
		List<String> whereOperator = new ArrayList<>();
		whereOperator.add("=");

		List<Map<String, Object>> userMap = null;

		try {
			QueryRequest request = new QueryRequest();
			request.setTableName("user");
			request.setSelectAllColumns(true);
			request.setWhereConditions(whereCondition);
			request.setWhereConditionsValues(whereConditionValues);
			request.setWhereOperators(whereOperator);
			userMap = daoCaller.selectHandler(request);
		} catch (CustomException e) {
			e.printStackTrace();
			return null;
		}
		
		if(userMap != null && !userMap.isEmpty())
		{
			Boolean check=Encryption.verifyPassword(password, (String) userMap.get(0).get("password"));
			System.out.println(check);
			if(!check)
			{
				throw new CustomException("Wrong Password");
			}
			return userMap.get(0);
		}
		return null;
	}
	
	public Map<String, Object> getCustomer(Long customer_id) throws CustomException
	{
		Validator.checkInvalidInput(customer_id);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("customer");
		List<String> whereCondition = new ArrayList<String>(), whereOperator = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<Object>();
		whereCondition.add("customer_id");
		whereConditionValue.add(customer_id);
		whereOperator.add("=");
		request.setWhereConditions(whereCondition);
		request.setWhereOperators(whereOperator);
		request.setWhereConditionsValues(whereConditionValue);
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		try {
			accountMap = daoCaller.selectHandler(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return accountMap.get(0);
	}
	
	public Map<String, Object> getEmployee(Long employee_id) throws CustomException
	{
		Validator.checkInvalidInput(employee_id);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("employee");
		List<String> whereCondition = new ArrayList<String>(), whereOperator = new ArrayList<String>();;
		List<Object> whereConditionValue = new ArrayList<Object>();
		whereCondition.add("employee_id");
		whereConditionValue.add(employee_id);
		whereOperator.add("=");
		request.setWhereConditions(whereCondition);
		request.setWhereOperators(whereOperator);
		request.setWhereConditionsValues(whereConditionValue);
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		try {
			accountMap = daoCaller.selectHandler(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return accountMap.get(0);
	}
	
	public List<Map<String, Object>> getAccounts(Long user_id, String role, Long branch_id) throws CustomException
	{
		Validator.checkInvalidInput(user_id, role);
		
		QueryRequest request = new QueryRequest();
		
		request.setSelectAllColumns(true);
		request.setTableName("account");
		request.setJoinTableName("branch");
		List<String> whereCondition = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<Object>();
		List<String> whereOperator = new ArrayList<String>(), joinOperators =null;
		Map<String, String> joinConditions = new HashMap<String, String>();
		
		joinConditions.put("branch_id", "branch_id");
		
		if(role.equals("CUSTOMER")) {
			whereCondition.add("user_id");
			whereConditionValue.add(user_id);
		} else if(role.equals("EMPLOYEE")) {
			whereCondition.add("account.branch_id");
			whereConditionValue.add(branch_id);
		}
		whereOperator.add("=");
		joinOperators = whereOperator;
		
		request.setWhereConditions(whereCondition);
		request.setWhereOperators(whereOperator);
		request.setWhereConditionsValues(whereConditionValue);
		
		request.setJoinConditions(joinConditions);
		request.setJoinOperators(joinOperators);
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		
		try {
			accountMap = daoCaller.selectHandler(request);
			return accountMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, Object>> getTransactions(Long account_number, Long from_time, Long to_time) throws CustomException
	{
		Validator.checkInvalidInput(account_number, from_time, to_time);
		QueryRequest request = new QueryRequest();
		
		request.setSelectAllColumns(true);
		request.setTableName("transaction");
		
		List<String> whereCondition = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<>(); 
		List<String> whereOperator = new ArrayList<String>();
		List<String> whereLogicalOperator = new ArrayList<String>();
		
		whereCondition.add("account_number");
		whereConditionValue.add(account_number);
		whereCondition.add("timestamp");
		whereConditionValue.add(from_time);
		whereCondition.add("timestamp");
		whereConditionValue.add(to_time);
		
		whereOperator.add("=");
		whereOperator.add(">=");
		whereOperator.add("<=");
		
		whereLogicalOperator.add("AND");
		whereLogicalOperator.add("AND");
		
		request.setWhereConditions(whereCondition);
		request.setWhereConditionsValues(whereConditionValue);
		request.setWhereOperators(whereOperator);
		request.setWhereLogicalOperators(whereLogicalOperator);
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> transactionMap = null;
		
		try {
			transactionMap = daoCaller.selectHandler(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return transactionMap;
	}
	
	public Long getBalance(Long account_number) throws CustomException	
	{
		Validator.checkInvalidInput(account_number);
		QueryRequest request = new QueryRequest();
		
		List<String> selectColumns = new ArrayList<String>();
		selectColumns.add("balance");
		List<String> whereCondition = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<>(); 
		whereCondition.add("account_number");
		whereConditionValue.add(account_number);
		List<String> whereOperator = new ArrayList<String>();
		whereOperator.add("=");
		
		request.setSelectColumns(selectColumns);
		request.setJoinTableName("account");
		request.setWhereConditions(whereCondition);
		request.setWhereConditionsValues(whereConditionValue);
		request.setWhereOperators(whereOperator);
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		
		try {
			accountMap = daoCaller.selectHandler(request);
			return (Long) accountMap.get(0).get("balance");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Map<String, Object> getProfile(Long customerId) throws CustomException
	{
		Validator.checkInvalidInput(customerId);
		QueryRequest request = new QueryRequest();
		
		request.setSelectAllColumns(true);
		request.setTableName("user");
		
		List<String> whereCondition = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<>(); 
		List<String> whereOperator = new ArrayList<String>();
		List<String> whereLogicalOperator = new ArrayList<String>();
		Map<String, String> joinCondition = new HashMap<String, String>();
		List<String> joinOperator = new ArrayList<String>();
		
		joinCondition.put("user_id", "customer_id");
		joinOperator.add("=");
		whereCondition.add("user_id");
		whereConditionValue.add(customerId);
		
		whereOperator.add("=");
		whereOperator.add("=");
		
		whereLogicalOperator.add("AND");
		
		request.setJoinConditions(joinCondition);
		request.setJoinTableName("customer");
		request.setJoinOperators(joinOperator);
		request.setWhereConditions(whereCondition);
		request.setWhereConditionsValues(whereConditionValue);
		request.setWhereOperators(whereOperator);
		request.setWhereLogicalOperators(whereLogicalOperator);

		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> transactionMap = null;
		
		try {
			transactionMap = daoCaller.selectHandler(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return transactionMap.get(0);
	}

	private void storeTransaction(Long from_account, Long to_account, Long user_id, Long amount, Long from_account_balance, Long to_account_balance) throws Exception
	{
		Validator.checkInvalidInput(from_account, to_account, user_id, amount, from_account, to_account_balance);
		DaoHandler<Transaction> transactionHandle = new DaoHandler<Transaction>();
		
		Transaction transaction_acc_one = new Transaction();
		transaction_acc_one.setAccountNumber(from_account);
		transaction_acc_one.setBalance(from_account_balance);
		transaction_acc_one.setModifiedBy(from_account);
		transaction_acc_one.setTimestamp(System.currentTimeMillis());
		transaction_acc_one.setTransactionAccount(to_account);
		transaction_acc_one.setTransactionAmount(amount);
		transaction_acc_one.setUserId(user_id);
		Long ref_number = transactionHandle.insertHandler(transaction_acc_one);
		
		Transaction transaction_acc_two = new Transaction();
		transaction_acc_two.setAccountNumber(to_account);
		transaction_acc_two.setBalance(to_account_balance);
		transaction_acc_two.setModifiedBy(from_account);
		transaction_acc_two.setTimestamp(System.currentTimeMillis());
		transaction_acc_two.setTransactionAccount(to_account);
		transaction_acc_two.setTransactionAmount(amount);
		transaction_acc_two.setUserId(user_id);
		transaction_acc_two.setReferenceNumber(ref_number);
		transactionHandle.insertHandler(transaction_acc_two);
	}
	
	public void makeTransaction(Long from_account, Long user_id, Long to_account, Long amount) throws Exception {
		Validator.checkInvalidInput(from_account, user_id, to_account, amount);
		Long from_account_balance = getBalance(from_account);
		Long to_account_balance = getBalance(to_account);
		
		if(from_account_balance >= amount) {
			throw new Exception("Balance Not Enough");
		}
		
		from_account_balance -= amount;
		to_account_balance += amount;
		
		QueryRequest acc_bal = new QueryRequest();
		
		acc_bal.setTableName("account");
		
		List<String> updates = new ArrayList<>();
		updates.add("balance");
		List<String> whereConditions = new ArrayList<>();
		List<Object> whereConditionsValues = new ArrayList<>();
		whereConditions.add("account_number");
		whereConditionsValues.add(from_account);
		List<String> whereOperators = new ArrayList<String>();
		whereOperators.add("=");
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		
		Map<String, Object> account_one = new HashMap<>();
		account_one.put("balance", from_account_balance);
		Map<String, Object> account_two = new HashMap<>();
		account_two.put("balance", to_account_balance);
		
		try {
			daoCaller.updateHandler(account_one, Account.class, whereConditions, whereConditionsValues, whereOperators, null);
			whereConditionsValues.remove(0);
			whereConditionsValues.add(to_account);
			daoCaller.updateHandler(account_two, Account.class, whereConditions, whereConditionsValues, whereOperators, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed Transaction");
		}
		
		
		storeTransaction(from_account, to_account, user_id, amount, from_account_balance, to_account_balance);
	}
	
	public void makeDepositWithdraw(Long from_account, Long user_id, Long amount, String process) throws Exception {
		Validator.checkInvalidInput(from_account, user_id, amount, process);
		Long from_account_balance = getBalance(from_account);
		
		if(process.equals("DEPOSIT"))
		{
			from_account_balance += amount;
		}
		else {
			if(from_account_balance >= amount) {
				throw new Exception("Balance Not Enough");
			}
			from_account_balance -= amount;
		}
		DaoHandler<Transaction> transactionHandle = new DaoHandler<Transaction>();		
		Transaction transaction_acc_one = new Transaction();
		transaction_acc_one.setAccountNumber(from_account);
		transaction_acc_one.setBalance(from_account_balance);
		transaction_acc_one.setModifiedBy(from_account);
		transaction_acc_one.setTimestamp(System.currentTimeMillis());
		transaction_acc_one.setTransactionAmount(amount);
		transaction_acc_one.setUserId(user_id);
		transactionHandle.insertHandler(transaction_acc_one);
	}
	
	public void deleteOrBlock(String type, String entity, String conditionValue) throws Exception
	{
		Validator.checkInvalidInput(type, entity, conditionValue);
		String tableName;
		String whereField;
		String status = null;
		
		if(type.equals("DELETE")) {
			status = "INACTIVE";
		} else if(type.equals("BLOCK")) {
			status = "BLOCKED";
		} else {
			throw new Exception("Wrong Type");
		}
		if(entity.equals("USER")) {
			tableName = "user";
			whereField = "user_id";
			DaoHandler<User> statusHandle = new DaoHandler<User>();
			statusHandle.deleteHandler(tableName, status, whereField, conditionValue);
		} else if(entity.equals("ACCOUNT")) {
			tableName = "account";
			whereField = "account_number";
			DaoHandler<Account> statusHandle = new DaoHandler<Account>();
			statusHandle.deleteHandler(tableName, status, whereField, conditionValue);
		} else {
			throw new Exception("Wrong Entity");
		}
	}
	
	public void createCustomer(String role, Map<String, Object> customerDetails) throws CustomException
	{
		if(role.equals("CUSTOMER"))
		{
			throw new CustomException("Not allowed");
		}
		String password =Encryption.hashPassword((String) customerDetails.get("password"));
		String name = (String) customerDetails.get("name");
		String email = (String) customerDetails.get("email");
		String mobile = (String) customerDetails.get("mobile");
		Date dateOfBirth = (Date) customerDetails.get("dob");
		Long modifiedBy = (Long) customerDetails.get("modifiedBy");
		Long aadharNumber = (Long) customerDetails.get("aadharNumber");
		String panNumber = (String) customerDetails.get("panNumber");

		Validator.checkInvalidInput(password, name, email, mobile, dateOfBirth, modifiedBy, aadharNumber, panNumber);

		Customer customer = new Customer();
		customer.setPassword(password);
		customer.setRole(Role.CUSTOMER);
		customer.setName(name);
		customer.setEmail(email);
		customer.setMobile(mobile);
		customer.setDateOfBirth(dateOfBirth);
		customer.setStatus(Status.ACTIVE);
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(modifiedBy);
		customer.setAadharNumber(aadharNumber);
		customer.setPanNumber(panNumber);
		customer.setModifiedTime(System.currentTimeMillis());
		
		DaoHandler<Customer> customerDao = new DaoHandler<Customer>();
		try {
			customerDao.insertHandler(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createBranch(String role, Map<String, Object> branchDetails) throws CustomException {
	    if (!role.equals("MANAGER")) {
	        throw new CustomException("Not allowed");
	    }

	    String name = (String) branchDetails.get("name");
	    Long ifsc = (Long) branchDetails.get("ifsc");
	    Long employeeId = (Long) branchDetails.get("employeeId");
	    String address = (String) branchDetails.get("address");
	    Long modifiedBy = (Long) branchDetails.get("modifiedBy");

	    Validator.checkInvalidInput(name, ifsc, employeeId, address);

	    Branch branch = new Branch();
	    branch.setName(name);
	    branch.setIfsc(ifsc);
	    branch.setEmployeeId(employeeId);
	    branch.setAddress(address);
	    branch.setCreationTime(System.currentTimeMillis());
	    branch.setModifiedBy(modifiedBy);

	    DaoHandler<Branch> branchDao = new DaoHandler<>();
	    try {
	        branchDao.insertHandler(branch);
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new CustomException("Error creating branch", e);
	    }
	}
	
	public void createEmployee(String role, Map<String, Object> employeeDetails) throws CustomException {
	    if (!role.equals("MANAGER")) {
	        throw new CustomException("Not allowed");
	    }

	    String password = (String) employeeDetails.get("password");
	    String name = (String) employeeDetails.get("name");
	    String email = (String) employeeDetails.get("email");
	    String mobile = (String) employeeDetails.get("mobile");
	    Date dateOfBirth = (Date) employeeDetails.get("dob");
	    Long modifiedBy = (Long) employeeDetails.get("modifiedBy");
	    Long branchId = (Long) employeeDetails.get("branchId");
	    String employeeRole = (String) employeeDetails.get("role");
	    
	    Validator.checkInvalidInput(password, name, email, mobile, dateOfBirth, modifiedBy);

	    Employee employee = new Employee();
	    employee.setPassword(password);
	    employee.setRole(Role.valueOf(employeeRole));
	    employee.setName(name);
	    employee.setEmail(email);
	    employee.setMobile(mobile);
	    employee.setDateOfBirth(dateOfBirth);
	    employee.setStatus(Status.ACTIVE);
	    employee.setModifiedBy(modifiedBy);
	    employee.setCreationTime(System.currentTimeMillis());
	    employee.setBranchId(branchId);

	    DaoHandler<Employee> employeeDao = new DaoHandler<>();
	    try {
	        employeeDao.insertHandler(employee);
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new CustomException("Error creating employee", e);
	    }
	}
	
	public void createAccount(String role, Map<String, Object> accountDetails) throws CustomException {
	    if (!role.equals("MANAGER") && !role.equals("EMPLOYEE")) {
	        throw new CustomException("Not allowed");
	    }

	    Long userId = (Long) accountDetails.get("userId");
	    Long branchId = (Long) accountDetails.get("branchId");
	    String accountType = (String) accountDetails.get("accountType");
	    Long balance = (Long) accountDetails.get("balance");
	    String status = (String) accountDetails.get("status");
	    Long modifiedBy = (Long) accountDetails.get("modifiedBy");

	    Validator.checkInvalidInput(userId, branchId, accountType, balance, status);

	    Account account = new Account();
	    account.setUserId(userId);
	    account.setBranchId(branchId);
	    account.setAccountType(accountType);
	    account.setDateOfOpening(System.currentTimeMillis());
	    account.setBalance(balance);
	    account.setStatus(Status.valueOf(status));
	    account.setDateOfOpening(System.currentTimeMillis());
	    account.setCreationTime(System.currentTimeMillis());
	    account.setModifiedBy(modifiedBy);

	    DaoHandler<Account> accountDao = new DaoHandler<>();
	    try {
	        accountDao.insertHandler(account);
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new CustomException("Error creating account", e);
	    }
	}
}
