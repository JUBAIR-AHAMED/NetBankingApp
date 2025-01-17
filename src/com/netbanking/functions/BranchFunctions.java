package com.netbanking.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.netbanking.dao.DataAccessObject;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Branch;
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;

public class BranchFunctions {
	public long createBranch(Branch branch) throws CustomException, Exception{
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		branch.setCreationTime(System.currentTimeMillis());
	    branch.setModifiedBy(userId);
	    Redis.deleteKeysWithStartString("BRANCH$COUNT");
	    DataAccessObject<Branch> accountDao = new DataAccessObject<>();
		return accountDao.insert(branch);
	}
	
	public List<Map<String, Object>> filteredGetBranch(Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		Integer offset = currentPage!=null? (currentPage - 1) * limit:null;
		String tableName = "branch";
		Boolean searchSimilar = (Boolean) filters.remove("searchSimilar");
		QueryRequest request = new QueryRequest()
									.setSelectAllColumns(true)
									.setTableName(tableName)
									.setCount((Boolean) filters.getOrDefault("count", false))
									.setOffset(offset)
									.setLimit(limit);
		
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
		request.setWhereConditionsType(whereConditionsType);
		DataAccessObject<Branch> daoCaller = new DataAccessObject<Branch>();
		List<Map<String, Object>> list = daoCaller.select(request);
		for(Map<String, Object> map:list) {
			Long id = (Long) map.get("branchId");
			String cacheKey = "BRANCH$BRANCH_ID:";
			cacheKey += id;
			Redis.setex(cacheKey, map);
		}
		return list;
	}
}