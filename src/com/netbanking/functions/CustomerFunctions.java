package com.netbanking.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.enums.Role;
import com.netbanking.enums.Status;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Customer;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Encryption;
import com.netbanking.util.Redis;

public class CustomerFunctions {
	@SuppressWarnings("unchecked")
	public Map<String, Object> get(Long id) throws CustomException, Exception
	{
		GetMetadata metadata = GetMetadata.CUSTOMER;
		String cacheKey = metadata.getCachKey()+id;
		cacheKey=cacheKey+id;
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
		DataAccessObject<Customer> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> resultList = daoCaller.select(request);
		Map<String, Object> map = (resultList == null || resultList.isEmpty()) ? null : resultList.get(0);
		Redis.setex(cacheKey, map);
		return map;
	}
	
	public long createCustomer(Customer customer, Long userId) throws Exception {
		customer.setPassword(Encryption.hashPassword(customer.getPassword()));
		customer.setCreationTime(System.currentTimeMillis());
		customer.setModifiedBy(userId);
		customer.setRole(Role.CUSTOMER);
		customer.setStatus(Status.ACTIVE);
		DataAccessObject<Customer> accountDao = new DataAccessObject<>();
		return accountDao.insert(customer);
	}
	
	public void updateCustomer(Customer customer, Long userId, Long key) throws Exception {
		if(customer==null) {
			return;
		}
		GetMetadata userMetadata = GetMetadata.USER;
		GetMetadata customerMetadata = GetMetadata.CUSTOMER;
		String cacheKeyUser = userMetadata.getCachKey()+key;
		String cacheKeyCustomer = customerMetadata.getCachKey()+key;
		System.out.println("Redis delete keys: "+cacheKeyUser+" "+cacheKeyCustomer);
		Redis.delete(cacheKeyCustomer);
		Redis.delete(cacheKeyUser);
		customer.setCustomerId(key);
		DataAccessObject<Customer> customerDao = new DataAccessObject<>();
		customerDao.update(customer);
	}
	
	public List<Map<String, Object>> getCustomers(Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		GetMetadata primaryTableMetaData = GetMetadata.USER;
		GetMetadata secondaryTableMetadata = GetMetadata.CUSTOMER;
		// offset for supporting pagination
		Integer offset = ApiHelper.getOffset(limit, currentPage);
		// required tables
		String primaryTableName = primaryTableMetaData.getTableName();
		String secondaryTableName = secondaryTableMetadata.getTableName();
		// starting to build the request
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(primaryTableName)
									.setCount((Boolean) filters.getOrDefault("count", false))
									.setOffset(offset)
									.setLimit(limit);
		filters.remove("count");
		// If moreDetails is required then send employee/customer detail in addition
		Boolean moreDetails = (Boolean) filters.remove("moreDetails");
		if(moreDetails) {
			Join join = new Join();
			join.putLeftTable(primaryTableName).putLeftColumn(primaryTableMetaData.getPrimaryKeyColumn())
				.putRightTable(secondaryTableName).putRightColumn(secondaryTableMetadata.getPrimaryKeyColumn())
				.putOperator("=").setTableName(secondaryTableName);
			request.putJoinConditions(join);
		}
		// adding filters passed to the method
		List<Where> whereConditions = new ArrayList<Where>();
		Boolean searchSimilar = (Boolean) filters.remove("searchSimilar");
		int filtersAdded=0;
		for(Map.Entry<String, Object> filter:filters.entrySet()) {
			if(filtersAdded>0) {
				request.putWhereLogicalOperators("AND");
			}
			String filterValue = filter.getValue().toString();
			String filterOperator = "=";
			if(searchSimilar!=null&&searchSimilar) {
				filterValue = "%"+filterValue+"%";
				filterOperator = "LIKE";
			}
			whereConditions.add(new Where(filter.getKey(), primaryTableName, filterValue));
			request.putWhereOperators(filterOperator);
			filtersAdded++;
		}
		// adding filter - role for fetching only customers
		whereConditions.add(new Where("role", primaryTableName, secondaryTableMetadata.toString()));
		request.putWhereOperators("=");
		if(filtersAdded>0) {
			request.putWhereLogicalOperators("AND");
		}
		request.setWhereConditionsType(whereConditions);
		// initiating query
		DataAccessObject<Customer> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> list = daoCaller.select(request);
		// storing in cache
		String cacheKey = null;
		if(moreDetails) {
			cacheKey = secondaryTableMetadata.getCachKey();
		} else {
			cacheKey = primaryTableMetaData.getCachKey();
		}
		Redis.setList(cacheKey, primaryTableMetaData.getPrimaryKeyColumn(), list);
		return list;
	}
}
