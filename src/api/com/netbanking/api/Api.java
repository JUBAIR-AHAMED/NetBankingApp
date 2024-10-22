package com.netbanking.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.netbanking.dao.DaoHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Account;
import com.netbanking.object.QueryRequest;
import com.netbanking.object.User;

public class Api {
	public Map<String, Object> getLogin(Long user_id, String password) {
		DaoHandler<User> daoCaller = new DaoHandler<User>();
		
		Map<String, Object> whereCondition = new HashMap<>();
		whereCondition.put("user_id", user_id);
		whereCondition.put("password", password);
		
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
		
		Map<String, Object> whereCondition = null;
		List<String> whereOperator = null;
		
		if(!role.equals("MANAGER")) {
			whereCondition = new HashMap<String, Object>();
			whereOperator = new ArrayList<String>();
			
			if(role.equals("CUSTOMER")) {
				whereCondition.put("user_id", user_id);
			} else if(role.equals("EMPLOYEE")) {
				whereCondition.put("branch_id", branch_id);
			}
			whereOperator.add("=");
			request.setWhereConditions(whereCondition);
			request.setWhereOperators(whereOperator);
		}
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> accountMap = null;
		
		try {
			accountMap = daoCaller.select(request);
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
		
		Map<String, Object> whereCondition = new HashMap<String, Object>();
		List<String> whereOperator = new ArrayList<String>();
		List<String> whereLogicalOperator = new ArrayList<String>();
		
		whereCondition.put("account_number", account_number);
		whereCondition.put("timestamp", from_time);
		whereCondition.put("timestamp", to_time);
		
		whereOperator.add("=");
		whereOperator.add(">=");
		whereOperator.add("<=");
		
		whereLogicalOperator.add("AND");
		whereLogicalOperator.add("AND");
		
		request.setWhereConditions(whereCondition);
		request.setWhereOperators(whereOperator);
		request.setWhereLogicalOperators(whereLogicalOperator);
		
		DaoHandler<Account> daoCaller = new DaoHandler<Account>();
		List<Map<String, Object>> transactionMap = null;
		
		try {
			transactionMap = daoCaller.select(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return transactionMap;
	}
}
