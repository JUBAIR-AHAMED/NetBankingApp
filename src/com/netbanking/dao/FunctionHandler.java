package com.netbanking.dao;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Account;
import com.netbanking.object.Branch;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.object.Role;
import com.netbanking.object.Status;
import com.netbanking.object.Transaction;
import com.netbanking.object.User;
import com.netbanking.util.Encryption;
import com.netbanking.util.Validator;

public class FunctionHandler {
	public Map<String, Object> getUser(Long user_id) throws CustomException, Exception {
		Validator.checkInvalidInput(user_id);
		DaoImpl<User> daoCaller = new DaoImpl<User>();
		QueryRequest request = new QueryRequest();
		request.setTableName("user");
		request.setSelectAllColumns(true);		
		request.putWhereConditions("userId");
		request.putWhereConditionsValues(user_id);
		request.putWhereOperators("=");
		List<Map<String, Object>> listOfMap = daoCaller.select(request);
		return listOfMap == null? null : listOfMap.get(0);
	}
	
	public Map<String, Object> getCustomer(Long customer_id) throws CustomException, Exception
	{
		Validator.checkInvalidInput(customer_id);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("customer");
		request.putWhereConditions("customerId");
		request.putWhereConditionsValues(customer_id);
		request.putWhereOperators("=");
		DaoImpl<Account> daoCaller = new DaoImpl<Account>();
		List<Map<String, Object>> accountMap = null;
		accountMap = daoCaller.select(request);
		return accountMap.get(0);
	}
	
	public Map<String, Object> getEmployee(Long employee_id) throws Exception
	{
		Validator.checkInvalidInput(employee_id);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("employee");
		request.putWhereConditions("employeeId");
		request.putWhereConditionsValues(employee_id);
		request.putWhereOperators("=");
		DaoImpl<Account> daoCaller = new DaoImpl<Account>();
		List<Map<String, Object>> accountMap = null;
		accountMap = daoCaller.select(request);
		return accountMap.get(0);
	}
	
	public Map<String, Object> getAccount(Long accountNumber) throws Exception {
		Validator.checkInvalidInput(accountNumber);
		List<String> filterFields = new ArrayList<String>();
		List<Object> filterValues = new ArrayList<Object>();
		filterFields.add("accountNumber");
		filterValues.add(accountNumber);
		return  getAccounts(filterFields, filterValues, true).get(0);
	}
	
	public List<Map<String, Object>> getAccounts(List<String> filterFields, List<Object> filterValues, Boolean inactiveReq) throws Exception
	{
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("account");
		Join joinConditions = new Join();
		joinConditions.setTableName("branch");
		joinConditions.putLeftTable("account");
		joinConditions.putLeftColumn("branchId");
		joinConditions.putRightTable("branch");
		joinConditions.putRightColumn("branchId");
		joinConditions.putOperator("=");

		List<Where> whereConditionsType = new ArrayList<Where>();
		
		for(int i=0;i<filterFields.size();i++) {
			if(i>0) {
				request.putWhereLogicalOperators("AND");
			}
			String filterField = filterFields.get(i);
			String tableName = "account";
			if(filterField.equals("branchId"))
			{
				tableName = "branch";
			}
			whereConditionsType.add(new Where(filterField, tableName, filterValues.get(i)));
			request.putWhereOperators("=");
		}
		if(!inactiveReq) {
			whereConditionsType.add(new Where("status", "account", "INACTIVE"));
			request.putWhereLogicalOperators("AND");
			request.putWhereOperators("!=");
		}
		request.setWhereConditionsType(whereConditionsType);
		request.putJoinConditions(joinConditions);
		DaoImpl<Account> daoCaller = new DaoImpl<Account>();
		List<Map<String, Object>> accountMap = null;
		accountMap = daoCaller.select(request);
		return accountMap;
	}

	public List<Map<String, Object>> getTransactions(Long account_number, Long from_time, Long to_time) throws CustomException, Exception
	{
		Validator.checkInvalidInput(account_number, from_time, to_time);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("transaction");
		request.putWhereConditions("accountNumber", "timestamp", "timestamp");
		request.putWhereConditionsValues(account_number, from_time, to_time);
		request.putWhereLogicalOperators("=", ">=", "<=");
		request.putWhereOperators("AND", "AND");		
		DaoImpl<Account> daoCaller = new DaoImpl<Account>();
		List<Map<String, Object>> transactionMap = null;
		transactionMap = daoCaller.select(request);
		return transactionMap;
	}
	
	//Remove
	public Map<String, Object> getProfile(Long userId, String role) throws Exception
	{
		Validator.checkInvalidInput(userId, role);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("user");
		Join joinConditions = new Join();
		if(role.equals("CUSTOMER"))
		{
			joinConditions.setTableName("customer");
			joinConditions.putLeftTable("user");
			joinConditions.putLeftColumn("userId");
			joinConditions.putRightTable("customer");
			joinConditions.putRightColumn("customerId");
			joinConditions.putOperator("=");
			request.putJoinConditions(joinConditions);
		} else if(role.equals("MANAGER")||role.equals("EMPLOYEE")) {
			joinConditions.setTableName("employee");
			joinConditions.putLeftTable("user");
			joinConditions.putLeftColumn("userId");
			joinConditions.putRightTable("employee");
			joinConditions.putRightColumn("employeeId");
			joinConditions.putOperator("=");
			request.putJoinConditions(joinConditions);
		} else {
			throw new CustomException("Role of the user is undefined.");
		}
		request.putWhereConditions("userId");
		request.putWhereConditionsValues(userId);
		request.putWhereOperators("=");
		DaoImpl<Account> daoCaller = new DaoImpl<Account>();
		List<Map<String, Object>> transactionMap = null;
		transactionMap = daoCaller.select(request);
		return transactionMap.get(0);
	}

	private void storeTransaction(Long from_account, Long to_account, Long user_id, Float amount, Float from_account_balance, Float to_account_balance, String transactionType) throws Exception
	{
		Validator.checkInvalidInput(from_account, user_id, amount);
		DaoImpl<Transaction> transactionHandle = new DaoImpl<Transaction>();
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
	
	public void makeTransaction(Long from_account_number, Long to_account_number, Long user_id, Float amount, String transactionType) throws Exception {
		Validator.checkInvalidInput(from_account_number, user_id, amount);
		Map<String, Object> fromAccountMap = getAccount(from_account_number), toAccountMap = getAccount(to_account_number);;
		Float from_account_balance = (Float) fromAccountMap.get("balance");
		Float to_account_balance = null;
		
		if(!transactionType.equals("deposit")&&from_account_balance < amount) {
			throw new CustomException("Balance Not Enough");
		}
		
		if(transactionType.equals("same-bank"))
		{
			Validator.checkInvalidInput(to_account_number);
			to_account_balance = (Float) toAccountMap.get("balance");
			to_account_balance += amount;
		}
		if(transactionType.equals("deposit"))
		{
			from_account_balance += amount;
		} else {
			System.out.println(from_account_balance);
			from_account_balance -= amount;
			System.out.println(from_account_balance);
		}
		QueryRequest fromAccRequest = new QueryRequest();
		fromAccRequest.setTableName("account");
		fromAccRequest.putWhereConditions("accountNumber");
		fromAccRequest.putWhereConditionsValues(from_account_number);
		fromAccRequest.putWhereOperators("=");
//		fromAccRequest.putUpdateField("balance");
//		fromAccRequest.putUpdateValue(from_account_balance);
		Account from_account = new Account();
		from_account.setBalance(from_account_balance);
		DaoImpl<Account> daoCaller = new DaoImpl<Account>();
		daoCaller.update(from_account, fromAccRequest);
		if(transactionType.equals("same-bank"))
		{
			Account to_account = new Account();
			to_account.setBalance(to_account_balance);
			QueryRequest toAccRequest = new QueryRequest();
			toAccRequest.setTableName("account");
			toAccRequest.putWhereConditions("accountNumber");
			toAccRequest.putWhereConditionsValues(to_account_number);
			toAccRequest.putWhereOperators("=");
			daoCaller.update(to_account, toAccRequest);
		}
		storeTransaction(from_account_number, to_account_number, user_id, amount, from_account_balance, to_account_balance, transactionType);
	}
	
	public List<Map<String, Object>> getTransactionStatement(Long accountNumber, Long fromDate, Long toDate, Integer limit) throws Exception {
		Validator.checkInvalidInput(accountNumber);
		DaoImpl<Transaction> transactionHandle = new DaoImpl<Transaction>();		
		QueryRequest request = new QueryRequest();
		request.setTableName("transaction");
		request.setSelectAllColumns(true);
	    List<String> orderByColumn = new ArrayList<String>(), orderDirections = new ArrayList<String>();
	    request.putWhereConditions("accountNumber");
	    request.putWhereOperators("=");
	    request.putWhereConditionsValues(accountNumber);
		orderByColumn.add("timestamp");
		orderDirections.add("DESC");
		if(fromDate!=null) {
			request.putWhereConditions("timestamp", "timestamp");
			request.putWhereConditionsValues(fromDate, toDate);
			request.putWhereOperators(">=", "<=");
			request.putWhereLogicalOperators("AND", "AND");
		}
		request.setOrderByColumns(orderByColumn);
		request.setOrderDirections(orderDirections);
		if(limit!=null)
		{
			request.setLimit(limit);			
		}
		return transactionHandle.select(request);
	}
	
	//remove
//	public void actionHandler(String type, String entity, Long accountNumber) throws Exception
//	{
//		Validator.checkInvalidInput(type, entity, accountNumber);
//		String status = null;
//		if(type.equals("DELETE")) {
//			status = "INACTIVE";
//		} else if(type.equals("BLOCK")) {
//			status = "BLOCKED";
//		} else if(type.equals("UNBLOCK")){
//			status = "ACTIVE";
//		} else {
//			throw new Exception("Wrong Type");
//		}
//		QueryRequest request = new QueryRequest();
//		DaoImpl<User> statusHandle = new DaoImpl<User>();
//        request.setTableName("account");
//		request.putUpdateField("status");
//        request.putUpdateValue(status);
//        request.putWhereConditions("accountNumber");
//        request.putWhereConditionsValues(accountNumber);
//        request.putWhereOperators("=");
//		statusHandle.updateHandler(request);
//	}
	
	public Long createCustomer(Map<String, Object> customerDetails) throws CustomException, Exception
	{
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
		
		DaoImpl<Customer> customerDao = new DaoImpl<Customer>();
		return customerDao.insertHandler(customer);
	}
	
	public Long createBranch(Map<String, Object> branchDetails) throws Exception {
	    String name = (String) branchDetails.get("name");
	    Long ifsc = (Long) branchDetails.get("ifsc");
	    Long employeeId = (Long) branchDetails.get("employeeId");
	    String address = (String) branchDetails.get("address");
	    Long modifiedBy = (Long) branchDetails.get("modifiedBy");
	    Validator.checkInvalidInput(name, ifsc, address, employeeId, modifiedBy);
	    Branch branch = new Branch();
	    branch.setName(name);
	    branch.setIfsc(ifsc);
	    branch.setEmployeeId(employeeId);
	    branch.setAddress(address);
	    branch.setCreationTime(System.currentTimeMillis());
	    branch.setModifiedBy(modifiedBy);
	    DaoImpl<Branch> branchDao = new DaoImpl<>();
        return branchDao.insertHandler(branch);
	}
	
	public Long createEmployee(Map<String, Object> employeeDetails) throws Exception {
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

	    DaoImpl<Employee> employeeDao = new DaoImpl<>();
        return employeeDao.insertHandler(employee);
	}
	
	public Long createAccount(Map<String, Object> accountDetails) throws Exception {
	    Long userId = (Long) accountDetails.get("userId");
	    Long branchId = (Long) accountDetails.get("branchId");
	    String accountType = (String) accountDetails.get("accountType");
	    Float balance = (Float) accountDetails.get("balance");
	    String status = (String) accountDetails.get("status");
	    Long modifiedBy = (Long) accountDetails.get("modifiedBy");
	    if(!accountType.equals("SAVINGS")&&!accountType.equals("CURRENT"))
	    {
	    	throw new CustomException("Account type is invalid.");
	    }
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

	    DaoImpl<Account> accountDao = new DaoImpl<>();
        return accountDao.insertHandler(account);
	}
}