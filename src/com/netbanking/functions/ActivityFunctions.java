package com.netbanking.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.object.Account;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Redis;
import com.netbanking.util.Validator;

public class ActivityFunctions {
	private static ActivityFunctions instance;
	
	private ActivityFunctions() {
	}
	
	public static ActivityFunctions getInstance() {
		if(Validator.isNull(instance)) {
			instance = new ActivityFunctions();
		}
		return instance;
	}
	
	public List<Map<String, Object>> filteredGetActivity(Map<String, Object> filters, Integer limit, Integer currentPage) throws Exception
	{
		Integer offset = ApiHelper.getOffset(limit, currentPage);
		GetMetadata metadata = GetMetadata.ACTIVITY;
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
