package com.netbanking.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.enums.Role;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Account;
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;

public class AccountFunctions {
	@SuppressWarnings("unchecked")
	public Map<String, Object> get(Long id) throws CustomException, Exception
	{
		GetMetadata metadata = GetMetadata.ACCOUNT;
		String cacheKey = metadata.getCachKey()+id;
		ObjectMapper objectMapper = new ObjectMapper();
		String cachedData = Redis.get(cacheKey);
		if(cachedData!=null) {
			return objectMapper.readValue(cachedData, Map.class);
		}
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(metadata.getTableName())
									.putWhereConditions(metadata.getPrimaryKeyColumn())
									.putWhereConditionsValues(id)
									.putWhereOperators("=");
		DataAccessObject<Account> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> resultList = daoCaller.select(request);
		Map<String, Object> map = (resultList == null || resultList.isEmpty()) ? null : resultList.get(0);
		Redis.setex(cacheKey, map);
		return map;
	}
	
	public long createAccount(Account account) throws CustomException, Exception {
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		account.setDateOfOpening(System.currentTimeMillis());
	    account.setCreationTime(System.currentTimeMillis());
	    account.setModifiedBy(userId);
	    DataAccessObject<Account> accountDao = new DataAccessObject<>();
		return accountDao.insert(account);
	}
	
	public void updateAccount(Account account, Long key) throws Exception {
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		if(account==null) {
			return;
		}
		GetMetadata metadata = GetMetadata.ACCOUNT;
		String cacheKey = metadata.getCachKey()+key;
		Redis.delete(cacheKey);
		account.setAccountNumber(key);
		account.setCreationTime(System.currentTimeMillis());
		account.setModifiedBy(userId);
		DataAccessObject<Account> accountDao = new DataAccessObject<>();
		accountDao.update(account);
	}
	
	public List<Map<String, Object>> filteredGetAccount(Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		UserDetailsLocal store = UserDetailsLocal.get();
		Role role = store.getRole();
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;		
		GetMetadata metadata = GetMetadata.ACCOUNT;
		String tableName = metadata.getTableName();
		@SuppressWarnings("unchecked")
		Set<String> searchSimilarFields = (Set<String>) filters.remove("searchSimilarFields");
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(tableName)
									.setCount((Boolean) filters.getOrDefault("count", false))
									.setOffset(offset)
									.setLimit(limit);
		List<Where> whereConditionsType = new ArrayList<Where>();
		if(role.equals(Role.EMPLOYEE)) {
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
		Redis.setList(	metadata.getCachKey(), 
						metadata.getPrimaryKeyColumn(), 
						list	);
		return list;	
	}
	
}
