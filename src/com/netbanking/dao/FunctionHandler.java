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
import com.netbanking.object.Branch;
import com.netbanking.object.Customer;
import com.netbanking.object.Transaction;
import com.netbanking.util.TableHelper;
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

	public List<Map<String, Object>> getAccount(Map<String, Object> filters, Integer limit, Integer offset) throws Exception
	{
		String tableName = "account";
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName(tableName);
		if(filters.containsKey("count")) {
			request.setCount((Boolean) filters.get("count"));
		}
		
		if(offset!=null) {
			request.setOffset(offset);
		}
		
		List<Where> whereConditionsType = new ArrayList<Where>();

		if(!(Boolean) filters.get("isInActiveRequired")) {
			whereConditionsType.add(new Where("status", tableName, "INACTIVE"));
			request.putWhereLogicalOperators("AND");
			request.putWhereOperators("!=");
		}
		filters.remove("isInActiveRequired");
		
		int i=0;
		for(Map.Entry<String, Object> filter:filters.entrySet()) {
			if(i>0) {
				request.putWhereLogicalOperators("AND");
			}
			if(filter.getKey().equals("count")) {
				continue;
			}
			whereConditionsType.add(new Where(filter.getKey(), tableName, "%"+filter.getValue()+"%"));
			request.putWhereOperators("LIKE");
			i++;
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
	
	public List<Map<String, Object>> getBranch(Map<String, Object> filters, Integer limit, Integer offset) throws Exception
	{
		String tableName = "branch";
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName(tableName);
		if(filters.containsKey("count")) {
			request.setCount((Boolean) filters.get("count"));
		}
		
		if(offset!=null) {
			request.setOffset(offset);
		}
		
		List<Where> whereConditionsType = new ArrayList<Where>();
		
		int i=0;
		for(Map.Entry<String, Object> filter:filters.entrySet()) {
			if(i>0) {
				request.putWhereLogicalOperators("AND");
			}
			if(filter.getKey().equals("count")) {
				continue;
			}
			whereConditionsType.add(new Where(filter.getKey(), tableName, filter.getValue()));
			request.putWhereOperators("=");
			i++;
		}
		if(limit!=null) {
			request.setLimit(limit);
		}
		request.setWhereConditionsType(whereConditionsType);
		DataAccessObject<Branch> daoCaller = new DataAccessObject<Branch>();
		List<Map<String, Object>> branchMap = null;
		branchMap = daoCaller.select(request);
		return branchMap;
	}
	
	public List<Map<String, Object>> getUser(Map<String, Object> filters, Integer limit, Integer offset, Class<? extends Model> clazz) throws Exception
	{
		String primaryTableName = "user";
		String secondaryTableName = TableHelper.getTableName(clazz);
		Boolean moreDetails = (Boolean) filters.remove("moreDetails");
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true);
		request.setTableName(primaryTableName);
		
		if(moreDetails) {
			Join join = new Join();
			join.putLeftTable(primaryTableName);
			join.putLeftColumn("userId");
			join.putRightTable(secondaryTableName);
			join.putRightColumn(clazz.equals(Customer.class)? "customerId" : "employeeId");
			join.putOperator("=");
			join.setTableName(secondaryTableName);
			request.putJoinConditions(join);
		}
		
		if(filters.containsKey("count")) {
			request.setCount((Boolean) filters.get("count"));
		}
		
		if(offset!=null) {
			request.setOffset(offset);
		}
		
		List<Where> whereConditionsType = new ArrayList<Where>();
		
		int i=0;
		for(Map.Entry<String, Object> filter:filters.entrySet()) {
			if(i>0) {
				request.putWhereLogicalOperators("AND");
			}
			if(filter.getKey().equals("count")) {
				continue;
			}
			whereConditionsType.add(new Where(filter.getKey(), primaryTableName, "%"+filter.getValue()+"%"));
			request.putWhereOperators("LIKE");
			i++;
		}
		whereConditionsType.add(new Where("role", primaryTableName, "CUSTOMER"));
		if(clazz.equals(Customer.class)) {
			request.putWhereOperators("=");
		} else {
			request.putWhereOperators("!=");
		}
		if(i>0) {
			request.putWhereLogicalOperators("AND");
		}
		if(limit!=null) {
			request.setLimit(limit);
		}
		request.setWhereConditionsType(whereConditionsType);
		DataAccessObject<Customer> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> map = null;
		map = daoCaller.select(request);
		return map;
	}
	
//	public List<Map<String, Object>> getBranch(Map<String, Object> filters, Boolean inactiveReq, Integer limit, Integer offset) throws Exception
//	{
//		QueryRequest request = new QueryRequest();
//		request.setSelectAllColumns(true);
//		request.setTableName("branch");
//		if(filters.containsKey("count")) {
//			request.setCount((Boolean) filters.get("count"));
//		}
//		
//		if(offset!=null) {
//			request.setOffset(offset);
//		}
//		
//		List<Where> whereConditionsType = new ArrayList<Where>();
//		int i=0;
//		for(Map.Entry<String, Object> filter:filters.entrySet()) {
//			if(i>0) {
//				request.putWhereLogicalOperators("AND");
//			}
//			if(filter.getKey().equals("count")) {
//				continue;
//			}
//			whereConditionsType.add(new Where(filter.getKey(), "branch", filter.getValue()));
//			request.putWhereOperators("=");
//			i++;
//		}
//		if(!inactiveReq) {
//			whereConditionsType.add(new Where("status", "account", "INACTIVE"));
//			request.putWhereLogicalOperators("AND");
//			request.putWhereOperators("!=");
//		}
//		if(limit!=null) {
//			request.setLimit(limit);
//		}
//		request.setWhereConditionsType(whereConditionsType);
//		DataAccessObject<Account> daoCaller = new DataAccessObject<Account>();
//		List<Map<String, Object>> accountMap = null;
//		accountMap = daoCaller.select(request);
//		return accountMap;
//	}
	
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
	
	public void makeTransaction(Map<String, Object> fromAccountMap, Map<String, Object> toAccountMap, Long user_id, Float amount, String transactionType) throws Exception {
		Long from_account_number = (Long) fromAccountMap.get("accountNumber"), to_account_number = (Long) toAccountMap.get("accountNumber");
		Float fromAccountBalance, toAccountBalance = null;
		Object frombalance = fromAccountMap.get ("balance");

		if (frombalance instanceof Double) {
		    fromAccountBalance = ((Double) frombalance).floatValue();
		} else if (frombalance instanceof Float) {
		    fromAccountBalance = (Float) frombalance;
		} else {
		    throw new IllegalArgumentException("Unexpected type for balance: " + (frombalance != null ? frombalance.getClass().getName() : "null"));
		}

		if(!transactionType.equals("deposit")&&fromAccountBalance < amount) {
			throw new CustomException("Balance Not Enough");
		}
		
		if(transactionType.equals("same-bank"))
		{
			Validator.checkInvalidInput(to_account_number);
			Object toBalance = toAccountMap.get("balance");

			if (toBalance instanceof Double) {
			    toAccountBalance = ((Double) toBalance).floatValue();
			} else if (toBalance instanceof Float) {
			    toAccountBalance = (Float) toBalance;
			} else {
			    throw new IllegalArgumentException("Unexpected type for balance: " + 
			                                       (toBalance != null ? toBalance.getClass().getName() : "null"));
			}

			toAccountBalance += amount;
		}
		if(transactionType.equals("deposit"))
		{
			fromAccountBalance += amount;
		} else {
			fromAccountBalance -= amount;
		}
		QueryRequest fromAccRequest = new QueryRequest();
		fromAccRequest.setTableName("account");
		fromAccRequest.putWhereConditions("accountNumber");
		fromAccRequest.putWhereConditionsValues(from_account_number);
		fromAccRequest.putWhereOperators("=");
//		fromAccRequest.putUpdateField("balance");
//		fromAccRequest.putUpdateValue(from_account_balance);
		Account from_account = new Account();
		from_account.setBalance(fromAccountBalance);
		DataAccessObject<Account> daoCaller = new DataAccessObject<Account>();
		daoCaller.update(from_account, fromAccRequest);
		if(transactionType.equals("same-bank"))
		{
			Account to_account = new Account();
			to_account.setBalance(toAccountBalance);
			QueryRequest toAccRequest = new QueryRequest();
			toAccRequest.setTableName("account");
			toAccRequest.putWhereConditions("accountNumber");
			toAccRequest.putWhereConditionsValues(to_account_number);
			toAccRequest.putWhereOperators("=");
			daoCaller.update(to_account, toAccRequest);
		}
		storeTransaction(from_account_number, to_account_number, user_id, amount, fromAccountBalance, toAccountBalance, transactionType);
	}
}