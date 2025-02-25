package com.netbanking.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.netbanking.dao.DataAccessObject;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Branch;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Validator;

public class BranchFunctions {
	private static BranchFunctions instance;
	
	private BranchFunctions() {
	}
	
	public static BranchFunctions getInstance() {
		if(Validator.isNull(instance)) {
			instance = new BranchFunctions();
		}
		return instance;
	}
	
	public long createBranch(Branch branch) throws CustomException, Exception{
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		branch.setCreationTime(System.currentTimeMillis());
	    branch.setModifiedBy(userId);
	    DataAccessObject<Branch> accountDao = new DataAccessObject<>();
		return accountDao.insert(branch);
	}
	
	public List<Map<String, Object>> filteredGetBranch(Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		GetMetadata metadata = GetMetadata.BRANCH;
		Integer offset = ApiHelper.getOffset(limit, currentPage);
		String tableName = metadata.getTableName();
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
		Redis.setList(	metadata.getCachKey(), 
						metadata.getPrimaryKeyColumn(), 
						list  );
		return list;
	}
}