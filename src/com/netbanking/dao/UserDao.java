package com.netbanking.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.netbanking.model.Model;
import com.netbanking.object.QueryRequest;

public class UserDao<T extends Model> implements Dao<T> {
	
	public void userInsert(Map<String, Object> insertValues) throws SQLException {
		insert("users", insertValues);
    }
	
	public void userCustomerInsert(Map<String, Object> userValues, Map<String, Object> customerValues) throws SQLException {
		insert("users", userValues);
		insert("customer", customerValues);
    }
	
	public void userEmployeeInsert(Map<String, Object> userValues, Map<String, Object> employeeValues) throws SQLException {
		insert("users", userValues);
		insert("users", employeeValues);
	}
	
	public void delete(Map<String, Object> conditions) throws SQLException {
        delete("users", conditions);
    }
	
	public List<Map<String, Object>> select(List<String> selectColumns, 
            Map<String, Object> whereConditions, 
            List<String> whereOperators, 
            List<String> orderByColumns, 
            Integer limit, 
            String joinTableName, 
            Map<String, String> joinConditions,
            List<String> joinOperators,
            List<String> joinLogicalOperators) throws SQLException, Exception {
		
			QueryRequest request = new QueryRequest();
			request.setTableName("users"); // Corrected table name to "users"
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
		request.setTableName("users");
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
