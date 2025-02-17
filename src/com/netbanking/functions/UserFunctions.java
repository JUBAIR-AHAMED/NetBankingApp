package com.netbanking.functions;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.exception.CustomException;
import com.netbanking.object.User;
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Validator;

public class UserFunctions {
	@SuppressWarnings("unchecked")
	public Map<String, Object> get(Long id) throws CustomException, Exception
	{
		GetMetadata metadata = GetMetadata.USER;
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
		DataAccessObject<User> daoCaller = new DataAccessObject<>();
		List<Map<String, Object>> resultList = daoCaller.select(request);
		Map<String, Object> map = (Validator.isNull(resultList) || resultList.isEmpty()) ? null : resultList.get(0);
		Redis.setex(cacheKey, map);
		return map;
	}
	
	public void updateUser(User user, Long key) throws Exception {
		GetMetadata metadata = GetMetadata.USER;
		GetMetadata customerMetadata = GetMetadata.CUSTOMER;
		GetMetadata employeeMetadata = GetMetadata.EMPLOYEE;
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		if(Validator.isNull(user)) {
			return;
		}
		String cacheKey = metadata.getCachKey()+key;
		String cacheKeyEmployee = employeeMetadata.getCachKey()+key;
		String cacheKeyCustomer = customerMetadata.getCachKey()+key;
		Redis.delete(cacheKey);
		Redis.delete(cacheKeyCustomer);
		Redis.delete(cacheKeyEmployee);
		user.setUserId(key);
		user.setModifiedBy(userId);
		user.setModifiedTime(System.currentTimeMillis());
		user.setModifiedBy(userId);
		DataAccessObject<User> userDao = new DataAccessObject<>();
		userDao.update(user);
	}
}
