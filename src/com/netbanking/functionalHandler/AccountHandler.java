package com.netbanking.functionalHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Account;
import com.netbanking.util.Redis;

public class AccountHandler {
	public long createAccount(Account account, Long userId) throws CustomException, Exception {
		account.setDateOfOpening(System.currentTimeMillis());
	    account.setCreationTime(System.currentTimeMillis());
	    account.setModifiedBy(userId);
	    Redis.deleteKeysWithStartString("ACCOUNT$COUNT");
	    DataAccessObject<Account> accountDao = new DataAccessObject<>();
		return accountDao.insert(account);
	}
	
	public void updateAccount(Account account, Long userId, Long key) throws Exception {
		if(account==null) {
			return;
		}
		String cacheKey = "ACCOUNT$ACCOUNT_NUMBER:"+key;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		Redis.deleteKeysWithStartString("ACCOUNT$COUNT");
		account.setAccountNumber(key);
		account.setCreationTime(System.currentTimeMillis());
		account.setModifiedBy(userId);
		DataAccessObject<Account> accountDao = new DataAccessObject<>();
		accountDao.update(account);
	}
	
	public List<Map<String, Object>> filteredGetAccount(Long userId, String role, Long branchId, Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;		
		String tableName = "account";
		@SuppressWarnings("unchecked")
		Set<String> searchSimilarFields = (Set<String>) filters.remove("searchSimilarFields");
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(tableName)
									.setCount((Boolean) filters.getOrDefault("count", false))
									.setOffset(offset)
									.setLimit(limit);
		List<Where> whereConditionsType = new ArrayList<Where>();
		if(role.equals("CUSTOMER")) {
			whereConditionsType.add(new Where("status", tableName, "INACTIVE"));
			request.putWhereLogicalOperators("AND")
					.putWhereOperators("!=");
		}
		int i=0;
		for(Map.Entry<String, Object> filter:filters.entrySet()) {
			if(filter.getKey().equals("count")) {
				continue;
			}
			if(i>0) {
				request.putWhereLogicalOperators("AND");
			}
			String filterKey = filter.getKey();
			String filterValue = filter.getValue().toString();
			String filterOperator = "=";
			if(searchSimilarFields!=null&&searchSimilarFields.contains(filterKey)) {
				filterValue = "%"+filter.getValue()+"%";
				filterOperator = "LIKE";
			}
			whereConditionsType.add(new Where(filter.getKey(), tableName, filterValue));
			request.putWhereOperators(filterOperator);
			i++;
		}
		request.setWhereConditionsType(whereConditionsType);
		DataAccessObject<Account> daoCaller = new DataAccessObject<Account>();
		List<Map<String, Object>> list = daoCaller.select(request);
		for(Map<String, Object> map:list) {
			Long id = (Long) map.get("accountNumber");
			String cacheKey = "ACCOUNT$ACCOUNT_NUMBER:";
			cacheKey += id;
			Redis.setex(cacheKey, map);
		}
		return list;
	}
	
}
