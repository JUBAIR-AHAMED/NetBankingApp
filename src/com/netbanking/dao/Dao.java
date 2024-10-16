package com.netbanking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.netbanking.exception.CustomException;
import com.netbanking.mapper.GenericMapper;
import com.netbanking.mapper.ReflectionMapper;
import com.netbanking.model.Model;
import com.netbanking.object.QueryRequest;
import com.netbanking.util.DBConnection;
import com.netbanking.util.TableHelper;

public interface Dao {
	//Insert operation
	default void insert(String tableName, Map<String, Object> insertValues) throws SQLException {
	    StringBuilder sql = new StringBuilder("INSERT INTO ");
	    sql.append(tableName).append(" (");

	    int fieldCount = insertValues.size();

	    int index = 0;
	    for (String field : insertValues.keySet()) {
	        sql.append(field);
	        if (index < fieldCount - 1) {
	            sql.append(", ");
	        }
	        index++;
	    }

	    sql.append(") VALUES (");

	    for (int i = 0; i < fieldCount; i++) {
	        sql.append("?");
	        if (i < fieldCount - 1) {
	            sql.append(", ");
	        }
	    }

	    sql.append(")");
	    try (Connection connection = DBConnection.getConnection();
	    	     PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

	    	    int parameterIndex = 1;
	    	    for (Object value : insertValues.values()) {
	    	        stmt.setObject(parameterIndex++, value);
	    	    }

	    	    stmt.executeUpdate();
	    	} 
	}
	
	//Delete operation
	default void delete(String tableName, Map<String, Object> conditions) throws SQLException {
	    StringBuilder sql = new StringBuilder("DELETE FROM ");
	    sql.append(tableName);
	    
	    if (!conditions.isEmpty()) {
	        sql.append(" WHERE ");
	        int index = 0;
	        for (String key : conditions.keySet()) {
	            sql.append(key).append(" = ?");
	            if (index < conditions.size() - 1) {
	                sql.append(" AND "); // Change to OR if necessary
	            }
	            index++;
	        }
	    }
	    try (Connection connection = DBConnection.getConnection();
	    	     PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

	    	    int parameterIndex = 1;
	    	    for (Object value : conditions.values()) {
	    	        stmt.setObject(parameterIndex++, value);
	    	    }

	    	    stmt.executeUpdate();
	    	}
	}
	
	//Update operation
	default void update(QueryRequest request) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ").append(request.getTableName());
        
        String joinTableName = request.getJoinTableName();
        Map<String, Object> whereConditions = request.getWhereConditions();
        Map<String, String> joinConditions = request.getJoinConditions();
        Map<String, Object> updates = request.getUpdates();
        
        if (joinTableName != null) {
            sql.append(" JOIN ").append(joinTableName).append(" ON ");
            appendJoinConditions(sql, joinConditions, request.getJoinOperators(), request.getJoinLogicalOperators());
        }

        sql.append(" SET ");
        appendUpdateValues(sql, updates);

        if (whereConditions != null && !whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            appendConditions(sql, whereConditions, request.getWhereOperators(), request.getWhereLogicalOperators());
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            setParameters(stmt, updates, whereConditions);
            stmt.executeUpdate();
        }
    }

    //Select operation
    default List<List<Object>> select(QueryRequest request) throws SQLException, Exception {
        StringBuilder sql = new StringBuilder("SELECT ");
        List<List<Object>> list = null;

        appendSelectColumns(sql, request.getSelectColumns());
        sql.append(" FROM ").append(request.getTableName());
        
        String joinTableName = request.getJoinTableName();
        
        if (joinTableName != null) {
            sql.append(" JOIN ").append(joinTableName).append(" ON ");
            appendJoinConditions(sql, request.getJoinConditions(), request.getJoinOperators(), request.getJoinLogicalOperators());
        }

        if (request.getWhereConditions() != null && !request.getWhereConditions().isEmpty()) {
            sql.append(" WHERE ");
            appendConditions(sql, request.getWhereConditions(), request.getWhereOperators(), request.getWhereLogicalOperators());
        }

        if (request.getOrderByColumns() != null && !request.getOrderByColumns().isEmpty()) {
            sql.append(" ORDER BY ").append(String.join(", ", request.getOrderByColumns()));
        }

        if (request.getLimit() != null) {
            sql.append(" LIMIT ").append(request.getLimit());
        }

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            setParameters(stmt, null, request.getWhereConditions());
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
            	if(list==null)
            	{
            		list = new ArrayList<>();
            	}
            	List<Object> tempList = new ArrayList<>();
            	for(String columnName : request.getSelectColumns())
            	{
            		tempList.add(rs.getObject(columnName));
            	}
            	list.add(tempList);
            }
            return list;
        }
        catch (Exception e) {
			throw new Exception("Failed getting the data.");
		}
        
    }

    // Helper methods for building the SQL query
    default void appendJoinConditions(StringBuilder sql, 
    								  Map<String, String> joinConditions, 
    								  List<String> joinOperators, 
    								  List<String> logicalOperators) {
        int index = 0;
        for (Map.Entry<String, String> entry : joinConditions.entrySet()) {
            sql.append(entry.getKey())
               .append(" ")
               .append(joinOperators.get(index))
               .append(" ")
               .append(entry.getValue());

            // Add logical operators (e.g., AND, OR) if it's not the last condition
            if (index < joinConditions.size() - 1) {
                sql.append(" ").append(logicalOperators.get(index)).append(" ");
            }
            index++;
        }
    }

    default void appendUpdateValues(StringBuilder sql, Map<String, Object> updates) {
        int index = 0;
        for (String key : updates.keySet()) {
            sql.append(key).append(" = ?");
            if (index < updates.size() - 1) {
                sql.append(", ");
            }
            index++;
        }
    }

    default void appendSelectColumns(StringBuilder sql, List<String> selectColumns) {
        if (selectColumns == null || selectColumns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", selectColumns));
        }
    }

    default void appendConditions(StringBuilder sql, Map<String, Object> conditions, List<String> operators, List<String> logicOperators) {
        int index = 0;
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            sql.append(entry.getKey())
               .append(" ")
               .append(operators.get(index))
               .append(" ?");

            // Add AND or OR if it exists and is not the last condition
            if (index < conditions.size() - 1 && index < logicOperators.size()) {
                sql.append(" ").append(logicOperators.get(index)).append(" ");
            }
            index++;
        }
    }

    default void setParameters(PreparedStatement stmt, Map<String, Object> updates, Map<String, Object> conditions) throws SQLException {
        int index = 1;
        if (updates != null) {
            for (Object value : updates.values()) {
                stmt.setObject(index++, value);
            }
        }
        if (conditions != null) {
            for (Object value : conditions.values()) {
                stmt.setObject(index++, value);
            }
        }
    }
}