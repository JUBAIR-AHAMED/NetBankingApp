package com.netbanking.functionalHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.model.Model;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.object.User;
import com.netbanking.util.Redis;

public class UserHandler {
	public void updateUser(User user, Long userId, Long key) throws Exception {
		if(user==null) {
			return;
		}
		String cacheKey = "USER$USER_ID:"+userId;
		if(Redis.exists(cacheKey)) {
			Redis.delete(cacheKey);
		}
		Redis.deleteKeysWithStartString("USER$COUNT");
		user.setModifiedBy(userId);
		user.setModifiedTime(System.currentTimeMillis());
		user.setModifiedBy(userId);
		DataAccessObject<User> userDao = new DataAccessObject<>();
		userDao.update(user);
	}
	
	public List<Map<String, Object>> filteredGetCustomerOrEmployee(Long userId, String role, Long branchId, Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		FunctionHandler functionHandler = new FunctionHandler();
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
		String userType = (String) filters.remove("userType");
		Class<? extends Model> clazz = null;
		if(userType.equals("employee")) {
			clazz = Employee.class;
		} else if(userType.equals("customer")) {
			clazz = Customer.class;
		}
		StringBuilder cacheKeyBuilder = new StringBuilder(clazz.getSimpleName().toUpperCase());
		String cacheKeyForRetrieval = null;
		cacheKeyForRetrieval = cacheKeyBuilder.append("$COUNT:").append(filters.getOrDefault("count", null))
												.append("$NAME:").append(filters.getOrDefault("name", null))
												.append("$USER_ID:").append(filters.getOrDefault("userId", null))
												.append("$EMAIL:").append(filters.getOrDefault("email", null))
												.toString();
		if(filters.containsKey("count")) {
			if(Redis.exists(cacheKeyForRetrieval)) {
				ObjectMapper objectMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> map = objectMapper.readValue(Redis.get(cacheKeyForRetrieval), Map.class);
				List<Map<String, Object>> list = new ArrayList<>();
				list.add(map);
				return list;
			}
		}
		List<Map<String, Object>> list = functionHandler.getUser(filters, limit, offset, clazz);
		for(Map<String, Object> map:list) {
			// As count is also maintained in this function
			if(filters.containsKey("count")) {
				Redis.setex(cacheKeyForRetrieval, map);
			}
			Long id = (Long) map.get("userId");
			String cacheKey = "USER$USER_ID:";
			cacheKey += id;
			if(Redis.exists(cacheKey)) {
				continue;
			}
			Redis.setex(cacheKey, map);
		}
		return list;
	}
}
