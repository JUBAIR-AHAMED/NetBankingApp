package com.netbanking.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.netbanking.object.QueryRequest;

public class CustomerDao implements Dao{
	public void insert(Map<String, Object> insertValues) throws SQLException {
		insert("customer", insertValues);
    }
	
	public void delete(Map<String, Object> conditions) throws SQLException {
        delete("customer", conditions);
    }
	
	public List<List<Object>> select(List<String> selectColumns, 
            Map<String, Object> whereConditions, 
            List<String> whereOperators, 
            List<String> orderByColumns, 
            Integer limit, 
            String joinTableName, 
            Map<String, String> joinConditions,
            List<String> joinOperators,
            List<String> joinLogicalOperators) throws SQLException, Exception {
		
			QueryRequest request = new QueryRequest();
			request.setTableName("customer");
			request.setSelectColumns(selectColumns);
			request.setWhereConditions(whereConditions);
			request.setWhereOperators(whereOperators);
			request.setOrderByColumns(orderByColumns);
			request.setLimit(limit);
			request.setJoinTableName(joinTableName);
			request.setJoinConditions(joinConditions);
			request.setJoinOperators(joinOperators);
			request.setJoinLogicalOperators(joinLogicalOperators);
			
			return select(request);
	}
	
	public void update(Map<String, Object> updates, 
            Map<String, Object> whereConditions, 
            String joinTableName, 
            Map<String, String> joinConditions,
            List<String> whereOperators,
            List<String> whereLogicalOperators,
            List<String> joinOperators,
            List<String> joinLogicalOperators) throws SQLException {
		QueryRequest request = new QueryRequest();
		request.setTableName("customer");
		request.setUpdates(updates);
		request.setWhereConditions(whereConditions);
		request.setJoinTableName(joinTableName);
		request.setJoinConditions(joinConditions);
		request.setWhereOperators(whereOperators);
		request.setWhereLogicalOperators(whereLogicalOperators);
		request.setJoinOperators(joinOperators);
		request.setJoinLogicalOperators(joinLogicalOperators);
		
		update(request);
	}

}