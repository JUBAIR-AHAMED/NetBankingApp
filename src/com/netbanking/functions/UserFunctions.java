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
import com.netbanking.exception.CustomException;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.object.User;
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;

public class UserFunctions {
	@SuppressWarnings("unchecked")
	public Map<String, Object> get(Long id) throws CustomException, Exception
	{
		String cacheKey = "USER$USER_ID:";
		cacheKey=cacheKey+id;
		ObjectMapper objectMapper = new ObjectMapper();
		String cachedData = Redis.get(cacheKey);
		if(cachedData!=null) {
			return objectMapper.readValue(cachedData, Map.class);
		}
		Class<?> type = User.class;
		GetMetadata metadata = GetMetadata.fromClass(type);
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(metadata.getTableName())
									.putWhereConditions(metadata.getPrimaryKeyColumn())
									.putWhereConditionsValues(id)
									.putWhereOperators("=");
		DataAccessObject<User> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> resultList = daoCaller.select(request);
		Map<String, Object> map = (resultList == null || resultList.isEmpty()) ? null : resultList.get(0);
		Redis.setex(cacheKey, map);
		return map;
	}
	
	public void updateUser(User user, Long key) throws Exception {
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		if(user==null) {
			return;
		}
		String cacheKey = "USER$USER_ID:"+userId;
		Redis.delete(cacheKey);
		user.setUserId(key);
		user.setModifiedBy(userId);
		user.setModifiedTime(System.currentTimeMillis());
		user.setModifiedBy(userId);
		DataAccessObject<User> userDao = new DataAccessObject<>();
		userDao.update(user);
	}
	
	public List<Map<String, Object>> filteredGetCustomerOrEmployee(Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		// offset for supporting pagination
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
		
		
		String userType = (String) filters.remove("userType");
		Class<? extends Model> clazz = null;		
		if(userType.equals("employee")) {
			clazz = Employee.class;
		} else if(userType.equals("customer")) {
			clazz = Customer.class;
		}
		String primaryTableName = "user";
		String secondaryTableName = YamlMapper.getTableName(clazz.getSimpleName());
		QueryRequest request = new QueryRequest();
		
		// If moreDetails present then send employee/customer detail in addition
		Boolean moreDetails = (Boolean) filters.remove("moreDetails");
		if(moreDetails) {
			Join join = new Join();
			join.putLeftTable(primaryTableName).putLeftColumn("userId")
				.putRightTable(secondaryTableName)
				.putRightColumn(clazz.equals(Customer.class)? "customerId" : "employeeId")
				.putOperator("=").setTableName(secondaryTableName);
			request.putJoinConditions(join);
		}
		
		List<Where> whereConditions = new ArrayList<Where>();
		Boolean searchSimilar = (Boolean) filters.remove("searchSimilar");
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
				whereConditions.add(new Where(filter.getKey(), secondaryTableName, filterValue));
			} else {
				whereConditions.add(new Where(filter.getKey(), primaryTableName, filterValue));
			}
			request.putWhereOperators(filterOperator);
			i++;
		}
		// setting role
		whereConditions.add(new Where("role", primaryTableName, "CUSTOMER"));
		if(clazz.equals(Customer.class)) {
			request.putWhereOperators("=");
		} else {
			request.putWhereOperators("!=");
		}
		if(i>0) {
			request.putWhereLogicalOperators("AND");
		}
		request.setSelectAllColumns(true)
				.setTableName(primaryTableName)
				.setCount((Boolean) filters.getOrDefault("count", false))
				.setOffset(offset)
				.setLimit(limit)
				.setWhereConditionsType(whereConditions);
		filters.remove("count");
		DataAccessObject<Customer> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> list = daoCaller.select(request);
		String cacheKey = "USER$USER_ID:";
		String id = "userId";
		Redis.setList(cacheKey, id, list);
		return list;
	}
	
	
}
