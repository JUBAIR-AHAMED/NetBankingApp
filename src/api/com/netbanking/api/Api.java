package com.netbanking.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.netbanking.dao.DaoHandler;
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
import com.netbanking.util.Validator;

public class Api {
	public Map<String, Object> getLogin(Long user_id, String password) {
		DaoHandler<User> daoCaller = new DaoHandler<User>();
		
		List<String> whereCondition = new ArrayList<>();
		whereCondition.add("user_id");
		whereCondition.add("password");
		
		List<Object> whereConditionValues = new ArrayList<>();
		whereConditionValues.add(user_id);
		whereConditionValues.add(password);
		
		List<String> whereOperator = new ArrayList<>();
		whereOperator.add("=");
		whereOperator.add("=");
		
		List<String> whereLogicalOperator = new ArrayList<>();
		whereLogicalOperator.add("AND");
		
		List<Map<String, Object>> userMap = null;
		
		try {
			QueryRequest request = new QueryRequest();
			request.setTableName("user");
			request.setSelectAllColumns(true);
			request.setWhereConditions(whereCondition);
			request.setWhereConditionsValues(whereConditionValues);
			request.setWhereOperators(whereOperator);
			request.setWhereLogicalOperators(whereLogicalOperator);
			userMap = daoCaller.selectHandler(request);
		} catch (CustomException e) {
			e.printStackTrace();
			return null;
		}
		
		if(userMap != null && !userMap.isEmpty())
		{
			return userMap.get(0);
		}
		return null;
	}
	
	public List<Map<String, Object>> getAccounts(Long user_id, String role, Long branch_id)
	{
		QueryRequest request = new QueryRequest();
		
		request.setSelectAllColumns(true);
		request.setTableName("account");
		
		List<String> whereCondition = null;
		List<Object> whereConditionValue = null;
		List<String> whereOperator = null;
		
		if(!role.equals("MANAGER")) {
			whereCondition = new ArrayList<String>();
			whereConditionValue = new ArrayList<Object>();
			whereOperator = new ArrayList<String>();
			
			if(role.equals("CUSTOMER")) {
				whereCondition.add("user_id");
				whereConditionValue.add(user_id);
			} else if(role.equals("EMPLOYEE")) {
				whereCondition.add("branch_id");
				whereConditionValue.add(branch_id);
			}
			whereOperator.add("=");
			request.setWhereConditions(whereCondition);
			request.setWhereOperators(whereOperator);
		}
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		
		try {
			accountMap = daoCaller.selectHandler(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return accountMap;
	}

	public List<Map<String, Object>> getTransactions(Long account_number, Long from_time, Long to_time)
	{
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
	
	public Long getBalance(Long account_number)	
	{
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
	
	public Map<String, Object> getProfile(Long customerId)
	{
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
		
		Account account_one = new Account();
		account_one.setBalance(from_account_balance);
		Account account_two = new Account();
		account_two.setBalance(to_account_balance);
		
		try {
			daoCaller.updateHandler(account_one, Account.class, updates, whereConditions, whereConditionsValues, whereOperators, null);
			whereConditionsValues.remove(0);
			whereConditionsValues.add(to_account);
			daoCaller.updateHandler(account_one, Account.class, updates, whereConditions, whereConditionsValues, whereOperators, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed Transaction");
		}
		
		
		storeTransaction(from_account, to_account, user_id, amount, from_account_balance, to_account_balance);
	}
	
	public void makeDepositWithdraw(Long from_account, Long user_id, Long amount, String process) throws Exception {
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
		String password = (String) customerDetails.get("password");
		String name = (String) customerDetails.get("name");
		String email = (String) customerDetails.get("email");
		String mobile = (String) customerDetails.get("mobile");
		Date dateOfBirth = (Date) customerDetails.get("dob");
		Long modifiedBy = (Long) customerDetails.get("modifiedBy");
		Long aadharNumber = (Long) customerDetails.get("aadharNumber");
		String panNumber = (String) customerDetails.get("panNumber");
		try {
			Validator.checkInvalidInput(password, name, email, mobile, dateOfBirth, modifiedBy, aadharNumber, panNumber);
		} catch (CustomException e) {
			throw e;
		}

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

	    try {
	        Validator.checkInvalidInput(name, ifsc, employeeId, address);
	    } catch (CustomException e) {
	        throw e;
	    }

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
	    try {
	        Validator.checkInvalidInput(password, name, email, mobile, dateOfBirth, modifiedBy);
	    } catch (CustomException e) {
	        throw e;
	    }

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

	    try {
	        Validator.checkInvalidInput(userId, branchId, accountType, balance, status);
	    } catch (CustomException e) {
	        throw e;
	    }

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
