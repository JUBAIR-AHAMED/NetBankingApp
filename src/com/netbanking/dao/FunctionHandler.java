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
		Validator.checkInvalidInput(user_id, password);
		DaoHandler<User> daoCaller = new DaoHandler<User>();
	
		List<String> whereCondition = new ArrayList<>();
		whereCondition.add("userId");
		
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
		whereCondition.add("customerId");
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
		whereCondition.add("employeeId");
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
	
	public boolean accountIsValid(Long accountNumber) throws CustomException {
		Validator.checkInvalidInput(accountNumber);
		
		QueryRequest request = new QueryRequest();
		
		request.setSelectAllColumns(true);
		request.setTableName("account");
		List<String> whereCondition = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<Object>();
		List<String> whereOperator = new ArrayList<String>();
		
		whereCondition.add("accountNumber");
		whereOperator.add("=");
		whereConditionValue.add(accountNumber);
		
		request.setWhereConditions(whereCondition);
		request.setWhereOperators(whereOperator);
		request.setWhereConditionsValues(whereConditionValue);
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		
		try {
			accountMap = daoCaller.selectHandler(request);
			if(accountMap==null||accountMap.isEmpty())
			{
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean accountAccessPermit(Long accountNumber, Long userId, String role, Long branchId) throws CustomException {
		if(!accountIsValid(accountNumber)) {
			throw new CustomException(accountNumber+" account is invalid.");
		}
		
		if(role.equals("MANAGER"))
		{
			return true;
		}
		QueryRequest request = new QueryRequest();
		
		request.setSelectAllColumns(true);
		request.setTableName("account");
		List<String> whereCondition = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<Object>();
		List<String> whereOperator = new ArrayList<String>();
		List<String> whereLogicalOperators = new ArrayList<String>();
		
		whereCondition.add("accountNumber");
		whereOperator.add("=");
		whereConditionValue.add(accountNumber);
		whereLogicalOperators.add("AND");
		if(role.equals("CUSTOMER"))
		{
			whereCondition.add("userId");
			whereOperator.add("=");
			whereConditionValue.add(userId);
		} else {
			whereCondition.add("branchId");
			whereOperator.add("=");
			whereConditionValue.add(branchId);
		}
		request.setWhereConditions(whereCondition);
		request.setWhereOperators(whereOperator);
		request.setWhereConditionsValues(whereConditionValue);
		request.setWhereLogicalOperators(whereLogicalOperators);
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		
		try {
			accountMap = daoCaller.selectHandler(request);
			if(accountMap==null||accountMap.isEmpty())
			{
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Map<String, Object>> getAccounts(Long user_id, String role, Long branch_id, String findField, Long findData) throws CustomException
	{
		Validator.checkInvalidInput(user_id, role);
		
		QueryRequest request = new QueryRequest();
		
		request.setSelectAllColumns(true);
		request.setTableName("account");
		request.setJoinTableName("branch");
		Map<String, String> whereConditionWithTable = new HashMap<>();
		List<Object> whereConditionValue = new ArrayList<Object>();
		List<String> whereOperator = new ArrayList<String>(), whereLogicalOperator = new ArrayList<String>(), joinOperators =null;
		Map<String, String> joinConditions = new HashMap<String, String>();
		
		joinConditions.put("branch_id", "branch_id");
		
		if(role.equals("CUSTOMER")) {
			whereConditionWithTable.put("userId", "account");
			whereConditionValue.add(user_id);
		} else if(role.equals("EMPLOYEE")||role.equals("MANAGER")) {
			String tableName = "account";
			if(findField.equals("branchId"))
			{
				tableName = "branch";
			}
			whereConditionWithTable.put(findField, tableName);
			whereConditionValue.add(findData);
		}
		whereOperator.add("=");
		whereOperator.add("=");
		joinOperators = whereOperator;
		
		request.setWhereConditionsWithTable(whereConditionWithTable);
		request.setWhereOperators(whereOperator);
		request.setWhereConditionsValues(whereConditionValue);
		request.setWhereLogicalOperators(whereLogicalOperator);
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
		
		whereCondition.add("accountNumber");
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
	
	public Float getBalance(Long account_number) throws CustomException
	{
		Validator.checkInvalidInput(account_number);
		QueryRequest request = new QueryRequest();
		
		List<String> selectColumns = new ArrayList<String>();
		selectColumns.add("balance");
		List<String> whereCondition = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<>(); 
		whereCondition.add("accountNumber");
		whereConditionValue.add(account_number);
		List<String> whereOperator = new ArrayList<String>();
		whereOperator.add("=");
		
		request.setSelectColumns(selectColumns);
		request.setTableName("account");
		request.setWhereConditions(whereCondition);
		request.setWhereConditionsValues(whereConditionValue);
		request.setWhereOperators(whereOperator);
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		
		accountMap = daoCaller.selectHandler(request);
		if(accountMap==null||accountMap.isEmpty())
		{
			throw new CustomException(account_number+" account is invalid.");
		}
		Object balanceValue = accountMap.get(0).get("balance");

        return ((Float) balanceValue).floatValue();
	}
	
	public Map<String, Object> getProfile(Long userId, String role) throws CustomException
	{
		Validator.checkInvalidInput(userId, role);
		QueryRequest request = new QueryRequest();
		
		request.setSelectAllColumns(true);
		request.setTableName("user");
		
		List<String> whereCondition = new ArrayList<String>();
		List<Object> whereConditionValue = new ArrayList<>(); 
		List<String> whereOperator = new ArrayList<String>();
		List<String> whereLogicalOperator = new ArrayList<String>();
		Map<String, String> joinCondition = new HashMap<String, String>();
		List<String> joinOperator = new ArrayList<String>();
		
		if(role.equals("CUSTOMER"))
		{
			joinCondition.put("user_id", "customer_id");
			request.setJoinTableName("customer");
		} else if(role.equals("MANAGER")||role.equals("EMPLOYEE")) {
			joinCondition.put("user_id", "employee_id");
			request.setJoinTableName("employee");
		} else {
			throw new CustomException("Role of the user is undefined.");
		}
		joinOperator.add("=");
		whereCondition.add("userId");
		whereConditionValue.add(userId);
		whereOperator.add("=");
		whereOperator.add("=");
		whereLogicalOperator.add("AND");
		
		request.setJoinConditions(joinCondition);
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

	private void storeTransaction(Long from_account, Long to_account, Long user_id, Float amount, Float from_account_balance, Float to_account_balance, String transactionType) throws Exception
	{
		Validator.checkInvalidInput(from_account, user_id, amount);
		DaoHandler<Transaction> transactionHandle = new DaoHandler<Transaction>();
		if(transactionType.equals("same-bank"))
		{
			Validator.checkInvalidInput(to_account, to_account_balance);
		}
		
		Transaction transaction_acc_one = new Transaction();
		transaction_acc_one.setAccountNumber(from_account);
		transaction_acc_one.setBalance(from_account_balance);
		transaction_acc_one.setModifiedBy(from_account);
		transaction_acc_one.setTimestamp(System.currentTimeMillis());
		transaction_acc_one.setTransactionAccount(to_account);
		transaction_acc_one.setTransactionAmount(amount);
		transaction_acc_one.setUserId(user_id);
		transaction_acc_one.setCreationTime(System.currentTimeMillis());
		if(transactionType.equals("same-bank")||transactionType.equals("other-bank")) {
			transaction_acc_one.setType("Debit");
		} else if(transactionType.equals("deposit")){
			transaction_acc_one.setType("Deposit");
		} else if(transactionType.equals("withdraw")) {
			transaction_acc_one.setType("Withdraw");
		} else {
			throw new CustomException("Invalid transaction type.");
		}
		Long ref_number = transactionHandle.insertHandler(transaction_acc_one);
		
		if(transactionType.equals("same-bank"))
		{
			Transaction transaction_acc_two = new Transaction();
			transaction_acc_two.setAccountNumber(to_account);
			transaction_acc_two.setBalance(to_account_balance);
			transaction_acc_two.setModifiedBy(from_account);
			transaction_acc_two.setTimestamp(System.currentTimeMillis());
			transaction_acc_two.setTransactionAccount(from_account);
			transaction_acc_two.setTransactionAmount(amount);
			transaction_acc_two.setUserId(user_id);
			transaction_acc_two.setReferenceNumber(ref_number);
			transaction_acc_two.setCreationTime(System.currentTimeMillis());
			transaction_acc_two.setType("Credit");
			transactionHandle.insertHandler(transaction_acc_two);
		}
	}
	
	public void makeTransaction(Long from_account, Long to_account, Long user_id, Float amount, String transactionType) throws Exception {
		Validator.checkInvalidInput(from_account, user_id, amount);
		Float from_account_balance = getBalance(from_account);
		Float to_account_balance = null;
		
		if(!transactionType.equals("deposit")&&from_account_balance < amount) {
			throw new CustomException("Balance Not Enough");
		}
		
		if(transactionType.equals("same-bank"))
		{
			Validator.checkInvalidInput(to_account);
			to_account_balance = getBalance(to_account);
			to_account_balance += amount;
		}
		if(transactionType.equals("deposit"))
		{
			from_account_balance += amount;
		} else {
			from_account_balance -= amount;
		}
		
		QueryRequest acc_bal = new QueryRequest();
		
		acc_bal.setTableName("account");
		
		List<String> updates = new ArrayList<>();
		updates.add("balance");
		List<String> whereConditions = new ArrayList<>();
		List<Object> whereConditionsValues = new ArrayList<>();
		whereConditions.add("accountNumber");
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
			if(transactionType.equals("same-bank"))
			{
				whereConditions.add("accountNumber");
				whereOperators.add("=");
				whereConditionsValues.add(to_account);
				daoCaller.updateHandler(account_two, Account.class, whereConditions, whereConditionsValues, whereOperators, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed Transaction");
		}
		
		
		storeTransaction(from_account, to_account, user_id, amount, from_account_balance, to_account_balance, transactionType);
	}
	
	public List<Map<String, Object>> getTransactionStatement(Long accountNumber, Long fromDate, Long toDate, Integer limit) throws CustomException {
		DaoHandler<Transaction> transactionHandle = new DaoHandler<Transaction>();		
		QueryRequest request = new QueryRequest();
		request.setTableName("transaction");
		request.setSelectAllColumns(true);
		List<String> whereConditions = new ArrayList<>();
		List<Object> whereConditionValues = new ArrayList<>();
		List<String> whereOperators = new ArrayList<String>();
		List<String> whereLogicalOperator = new ArrayList<String>();
	    List<String> orderByColumn = new ArrayList<String>(), orderDirections = new ArrayList<String>();
		whereConditions.add("accountNumber");
		whereOperators.add("=");
		whereConditionValues.add(accountNumber);
		orderByColumn.add("timestamp");
		orderDirections.add("DESC");
		if(fromDate!=null) {
			whereConditions.add("timestamp");
			whereConditions.add("timestamp");
			
			whereConditionValues.add(fromDate);
			whereConditionValues.add(toDate);

			whereOperators.add(">=");
			whereOperators.add("<=");

			whereLogicalOperator.add("AND");
			whereLogicalOperator.add("AND");
		}
		request.setOrderByColumns(orderByColumn);
		request.setOrderDirections(orderDirections);
		request.setWhereOperators(whereOperators);
		request.setWhereLogicalOperators(whereLogicalOperator);
		request.setWhereConditionsValues(whereConditionValues);
		request.setWhereConditions(whereConditions);
		if(limit!=null)
		{
			request.setLimit(limit);			
		}
		return transactionHandle.selectHandler(request);
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

	    String password = Encryption.hashPassword((String) employeeDetails.get("password"));
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
	    Float balance = (Float) accountDetails.get("balance");
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
