package com.netbanking.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.exception.CustomException;
import com.netbanking.model.Model;
import com.netbanking.object.Account;
import com.netbanking.object.Transaction;
import com.netbanking.util.Validator;

public class FunctionHandler {
	// Get Methods
	public <T extends Model> Map<String, Object> getRecord(Long id, Class<T> type) throws CustomException, Exception {
		Validator.checkInvalidInput(id);
		GetMetadata metadata = GetMetadata.fromClass(type);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName(metadata.getTableName());
		request.putWhereConditions(metadata.getPrimaryKeyColumn());
		request.putWhereConditionsValues(id);
		request.putWhereOperators("=");
		DataAccessObject<T> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> resultList = daoCaller.select(request);
		return (resultList == null || resultList.isEmpty()) ? null : resultList.get(0);
	}
		
	public List<Map<String, Object>> getTransactionStatement(Long accountNumber, Long fromDate, Long toDate, Integer limit, Integer offset, Boolean count) throws Exception {
		Validator.checkInvalidInput(accountNumber);
		DataAccessObject<Transaction> transactionHandle = new DataAccessObject<Transaction>();		
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
		if(offset!=null) {
			request.setOffset(offset);
		}
		if(count!=null) {
			request.setCount(count);
		}
		return transactionHandle.select(request);
	}

	public List<Map<String, Object>> getAccounts(List<String> filterFields, List<Object> filterValues, Boolean inactiveReq, Integer limit, Integer offset) throws Exception
	{
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("account");
		int countIndex = filterFields.indexOf("count");
		System.out.println(countIndex);
		if(countIndex!=-1) {
			request.setCount((Boolean) filterValues.get(countIndex));
		}
		
		if(offset!=null) {
			request.setOffset(offset);
		}
		
		List<Where> whereConditionsType = new ArrayList<Where>();
		
		for(int i=0;i<filterFields.size();i++) {
			if(i>0) {
				request.putWhereLogicalOperators("AND");
			}
			if(filterFields.get(i).equals("count")) {
				continue;
			}
			String filterField = filterFields.get(i);
			String tableName = "account";
			whereConditionsType.add(new Where(filterField, tableName, filterValues.get(i)));
			request.putWhereOperators("=");
		}
		if(!inactiveReq) {
			whereConditionsType.add(new Where("status", "account", "INACTIVE"));
			request.putWhereLogicalOperators("AND");
			request.putWhereOperators("!=");
		}
		if(limit!=null) {
			request.setLimit(limit);
		}
		request.setWhereConditionsType(whereConditionsType);
		DataAccessObject<Account> daoCaller = new DataAccessObject<Account>();
		List<Map<String, Object>> accountMap = null;
		accountMap = daoCaller.select(request);
		return accountMap;
	}
	
	public Map<String, Object> getProfile(Long userId, String role) throws Exception
	{
		Validator.checkInvalidInput(userId, role);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName("user");
//		Join joinConditions = new Join();
//		if(role.equals("CUSTOMER"))
//		{
//			joinConditions.setTableName("customer");
//			joinConditions.putLeftTable("user");
//			joinConditions.putLeftColumn("userId");
//			joinConditions.putRightTable("customer");
//			joinConditions.putRightColumn("customerId");
//			joinConditions.putOperator("=");
//		} else if(role.equals("MANAGER")||role.equals("EMPLOYEE")) {
//			joinConditions.setTableName("employee");
//			joinConditions.putLeftTable("user");
//			joinConditions.putLeftColumn("userId");
//			joinConditions.putRightTable("employee");
//			joinConditions.putRightColumn("employeeId");
//			joinConditions.putOperator("=");
//		} else {
//			throw new CustomException("Role of the user is undefined.");
//		}
//		request.putJoinConditions(joinConditions);
		request.putWhereConditions("userId");
		request.putWhereConditionsValues(userId);
		request.putWhereOperators("=");
		DataAccessObject<Account> daoCaller = new DataAccessObject<Account>();
		List<Map<String, Object>> transactionMap = null;
		transactionMap = daoCaller.select(request);
		return transactionMap.get(0);
	}
	
	//Create
	public Long create(Model object) throws Exception {
		DataAccessObject<Model> accountDao = new DataAccessObject<>();
		return accountDao.insertHandler(object);
	}
	
	//Update
	public void update(Model object, Class<?> clazz, Long id) throws Exception {
		DataAccessObject<Model> accountDao = new DataAccessObject<>();
		GetMetadata metadata = GetMetadata.fromClass(clazz);
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName(metadata.getTableName());
		request.putWhereConditions(metadata.getPrimaryKeyColumn());
		request.putWhereConditionsValues(id);
		request.putWhereOperators("=");
		accountDao.update(object, request);
	}
	
	private void storeTransaction(Long from_account, Long to_account, Long user_id, Float amount, Float from_account_balance, Float to_account_balance, String transactionType) throws Exception
	{
		Validator.checkInvalidInput(from_account, user_id, amount);
		DataAccessObject<Transaction> transactionHandle = new DataAccessObject<Transaction>();
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
		Map<String, Object> fromAccountMap = getRecord(from_account_number, Account.class), toAccountMap = getRecord(to_account_number, Account.class);
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
			from_account_balance -= amount;
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
		DataAccessObject<Account> daoCaller = new DataAccessObject<Account>();
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
}