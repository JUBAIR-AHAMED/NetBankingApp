package com.netbanking.functionalHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Branch;
import com.netbanking.util.Redis;

public class BranchHandler {
	public long createBranch(Branch branch, Long userId) throws CustomException, Exception{
		branch.setCreationTime(System.currentTimeMillis());
	    branch.setModifiedBy(userId);
	    Redis.deleteKeysWithStartString("BRANCH$COUNT");
	    DataAccessObject<Branch> accountDao = new DataAccessObject<>();
		return accountDao.insert(branch);
	}
	
	public List<Map<String, Object>> filteredGetBranch(Long userId, String role, Long branchId, Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		FunctionHandler functionHandler = new FunctionHandler();
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
		StringBuilder cacheKeyBuilder = new StringBuilder("BRANCH");
		String cacheKeyForRetrieval = null;
		cacheKeyForRetrieval = cacheKeyBuilder.append("$COUNT:").append(filters.getOrDefault("count", null))
												.append("$BRANCH_ID:").append(filters.getOrDefault("branchId", null))
												.append("$EMPLOYEE_ID:").append(filters.getOrDefault("employeeId", null))
												.append("$IFSC:").append(filters.getOrDefault("ifsc", null))
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
		List<Map<String, Object>> list = functionHandler.getBranch(filters, limit, offset);
		for(Map<String, Object> map:list) {
			// As count is also maintained in this function
			if(filters.containsKey("count")) {
				Redis.setex(cacheKeyForRetrieval, map);
			}
			Long id = (Long) map.get("branchId");
			String cacheKey = "BRANCH$BRANCH_ID:";
			cacheKey += id;
			if(Redis.exists(cacheKey)) {
				continue;
			}
			Redis.setex(cacheKey, map);
		}
		return list;
	}
}