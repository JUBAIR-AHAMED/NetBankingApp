package com.netbanking.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.exception.CustomException;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;
import com.netbanking.object.Account;
import com.netbanking.object.Branch;
import com.netbanking.object.Customer;
import com.netbanking.object.Transaction;
import com.netbanking.util.Converter;
import com.netbanking.util.Validator;

public class FunctionHandler {
	public <T extends Model> Map<String, Object> getRecord(Long id, Class<T> type) throws CustomException, Exception {
		Validator.checkInvalidInput(id);
		GetMetadata metadata = GetMetadata.fromClass(type);
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(metadata.getTableName())
									.putWhereConditions(metadata.getPrimaryKeyColumn())
									.putWhereConditionsValues(id)
									.putWhereOperators("=");
		DataAccessObject<T> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> resultList = daoCaller.select(request);
		return (resultList == null || resultList.isEmpty()) ? null : resultList.get(0);
	}
		
	public List<Map<String, Object>> getTransactionStatement(Long accountNumber, Long fromDate, Long toDate, Integer limit, Integer offset, Boolean count) throws Exception {
		Validator.checkInvalidInput(accountNumber);
		DataAccessObject<Transaction> transactionHandle = new DataAccessObject<Transaction>();		
		List<String> orderByColumn = new ArrayList<String>(), orderDirections = new ArrayList<String>();
		QueryRequest request = new QueryRequest()
									.setTableName("transaction")
									.setSelectAllColumns(true)
									.putWhereConditions("accountNumber")
									.putWhereOperators("=")
									.putWhereConditionsValues(accountNumber);
		
		orderByColumn.add("timestamp");
		orderDirections.add("DESC");
		if(fromDate!=null) {
			request.putWhereConditions("timestamp")
					.putWhereConditionsValues(fromDate)
					.putWhereOperators(">=")
					.putWhereLogicalOperators("AND");
		}
		if(toDate!=null) {
			request.putWhereConditions("timestamp")
					.putWhereConditionsValues(toDate)
					.putWhereOperators("<=")
					.putWhereLogicalOperators("AND");
		}
		request.setOrderByColumns(orderByColumn)
				.setOrderDirections(orderDirections);
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
		Boolean searchSimilar = (Boolean) filters.remove("searchSimilar");
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(tableName);
		if(filters.containsKey("count")) {
			request.setCount((Boolean) filters.get("count"));
		}
		if(offset!=null) {
			request.setOffset(offset);
		}
		List<Where> whereConditionsType = new ArrayList<Where>();
		if(!(Boolean) filters.get("isInActiveRequired")) {
			whereConditionsType.add(new Where("status", tableName, "INACTIVE"));
			request.putWhereLogicalOperators("AND")
					.putWhereOperators("!=");
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
			String filterValue = filter.getValue().toString();
			String filterOperator = "=";
			if(searchSimilar!=null&&searchSimilar) {
				filterValue = "%"+filter.getValue()+"%";
				filterOperator = "LIKE";
			}
			whereConditionsType.add(new Where(filter.getKey(), tableName, filterValue));
			request.putWhereOperators(filterOperator);
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
		Boolean searchSimilar = (Boolean) filters.remove("searchSimilar");
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true)
				.setTableName(tableName);
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
			String filterValue = filter.getValue().toString();
			String filterOperator = "=";
			if(searchSimilar!=null&&searchSimilar) {
				filterValue = "%"+filter.getValue()+"%";
				filterOperator = "LIKE";
			}
			whereConditionsType.add(new Where(filter.getKey(), tableName, filterValue));
			request.putWhereOperators(filterOperator);
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
		String secondaryTableName = YamlMapper.getTableName(clazz.getSimpleName());
		Boolean moreDetails = (Boolean) filters.remove("moreDetails");
		Boolean searchSimilar = (Boolean) filters.remove("searchSimilar");
		QueryRequest request = new QueryRequest();
		request.setSelectAllColumns(true)
			.setTableName(primaryTableName);
		
		if(moreDetails) {
			Join join = new Join();
			join.putLeftTable(primaryTableName).putLeftColumn("userId")
				.putRightTable(secondaryTableName)
				.putRightColumn(clazz.equals(Customer.class)? "customerId" : "employeeId")
				.putOperator("=").setTableName(secondaryTableName);
			request.putJoinConditions(join);
		}
		
		if(filters.containsKey("count")) {
			request.setCount((Boolean) filters.get("count"));
			filters.remove("count");
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
			String filterValue = filter.getValue().toString();
			String filterOperator = "=";
			if(searchSimilar!=null&&searchSimilar) {
				filterValue = "%"+filterValue+"%";
				filterOperator = "LIKE";
			}
			if(filter.getKey().equals("branchId")) {
				whereConditionsType.add(new Where(filter.getKey(), secondaryTableName, filterValue));
			} else {
				whereConditionsType.add(new Where(filter.getKey(), primaryTableName, filterValue));
			}
			request.putWhereOperators(filterOperator);
			i++;
		}
		// setting role
		whereConditionsType.add(new Where("role", primaryTableName, "CUSTOMER"));
		if(clazz.equals(Customer.class)) {
			request.putWhereOperators("=");
		} else {
			request.putWhereOperators("!=");
		}
		if(i>0) {
			request.putWhereLogicalOperators("AND");
		}
		// setting limit	
		if(limit!=null) {
			request.setLimit(limit);
		}
		request.setWhereConditionsType(whereConditionsType);
		DataAccessObject<Customer> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> map = null;
		map = daoCaller.select(request);
		return map;
	}
	
	//Create
	public Long create(Model object) throws Exception {
		DataAccessObject<Model> accountDao = new DataAccessObject<>();
		return accountDao.insert(object);
	}
	
	//Update
	public void update(Model object, Class<?> clazz, Long id) throws Exception {
		DataAccessObject<Model> accountDao = new DataAccessObject<>();
		GetMetadata metadata = GetMetadata.fromClass(clazz);
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(metadata.getTableName())
									.putWhereConditions(metadata.getPrimaryKeyColumn())
									.putWhereConditionsValues(id)
									.putWhereOperators("=");
		accountDao.update(object, request);
	}
	
	private void storeTransaction(Long from_account, Long to_account, 
			Long from_user_id, Long to_user_id,
			Long user_id, Float amount, 
			Float from_account_balance, Float to_account_balance, String transactionType) throws Exception
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
		transaction_acc_one.setModifiedBy(user_id);
		transaction_acc_one.setTimestamp(System.currentTimeMillis());
		transaction_acc_one.setTransactionAccount(to_account);
		transaction_acc_one.setTransactionAmount(amount);
		transaction_acc_one.setUserId(from_user_id);
		transaction_acc_one.setCreationTime(System.currentTimeMillis());
		if(transactionType.equalsIgnoreCase("same-bank")||transactionType.equalsIgnoreCase("other-bank")) {
			transaction_acc_one.setType("DEBIT");
		} else if(transactionType.equalsIgnoreCase("deposit")){
			transaction_acc_one.setType("DEPOSIT");
		} else if(transactionType.equalsIgnoreCase("withdraw")) {
			transaction_acc_one.setType("WITHDRAW");
		} 
		Long ref_number = transactionHandle.insert(transaction_acc_one);
		
		if(transactionType.equals("same-bank"))
		{
			Transaction transaction_acc_two = new Transaction();
			transaction_acc_two.setAccountNumber(to_account);
			transaction_acc_two.setBalance(to_account_balance);
			transaction_acc_two.setModifiedBy(user_id);
			transaction_acc_two.setTimestamp(System.currentTimeMillis());
			transaction_acc_two.setTransactionAccount(from_account);
			transaction_acc_two.setTransactionAmount(amount);
			transaction_acc_two.setUserId(to_user_id);
			transaction_acc_two.setReferenceNumber(ref_number);
			transaction_acc_two.setCreationTime(System.currentTimeMillis());
			transaction_acc_two.setType("CREDIT");
			transactionHandle.insert(transaction_acc_two);
		}
	}
	
	public void makeTransaction(Map<String, Object> fromAccountMap, Map<String, Object> toAccountMap, Long user_id, Float amount, String transactionType) throws Exception {
		Long from_account_number = (Long) fromAccountMap.get("accountNumber");
		Long to_account_number = null;
		if(toAccountMap!=null) {
			to_account_number = (Long) toAccountMap.getOrDefault("accountNumber", null);
		}
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
			throw new CustomException(422, "Balance Not Enough");
		}
		
		if(transactionType.equals("same-bank"))
		{
			Validator.checkInvalidInput(to_account_number);
			Object toBalance = toAccountMap.get("balance");
			if (toBalance instanceof Double) {
			    toAccountBalance = ((Double) toBalance).floatValue();
			} else if (toBalance instanceof Float) {
			    toAccountBalance = (Float) toBalance;
			}
			toAccountBalance += amount;
		}
		
		if(transactionType.equals("deposit"))
		{
			fromAccountBalance += amount;
		} else {
			fromAccountBalance -= amount;
		}
		QueryRequest fromAccRequest = new QueryRequest()
											.setTableName("account")
											.putWhereConditions("accountNumber")
											.putWhereConditionsValues(from_account_number)
											.putWhereOperators("=");
		Account from_account = new Account();
		from_account.setBalance(fromAccountBalance);
		DataAccessObject<Account> daoCaller = new DataAccessObject<Account>();
		daoCaller.update(from_account, fromAccRequest);
		if(transactionType.equals("same-bank"))
		{
			Account to_account = new Account();
			to_account.setBalance(toAccountBalance);
			QueryRequest toAccRequest = new QueryRequest()
											.setTableName("account")
											.putWhereConditions("accountNumber")
											.putWhereConditionsValues(to_account_number)
											.putWhereOperators("=");
			daoCaller.update(to_account, toAccRequest);
		}
		Long fromUserId = Converter.convertToLong(fromAccountMap.get("userId"));
		Long toUserId = null;
		if(toAccountMap!=null) {
			toUserId = Converter.convertToLong(toAccountMap.get("userId"));
		}
		storeTransaction(from_account_number, to_account_number,
				fromUserId, toUserId,
				user_id, amount, fromAccountBalance, 
				toAccountBalance, transactionType);
	}
}