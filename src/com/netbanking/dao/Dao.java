package com.netbanking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;
import com.netbanking.object.QueryRequest;
import com.netbanking.util.DBConnection;
import com.netbanking.util.QueryHelper;

public interface Dao<T> {
	
	//Insert operation
	default Long  insert(String tableName, Map<String, Object> insertValues) throws SQLException {
	    StringBuilder sql = new StringBuilder("INSERT INTO ");
	    sql.append(tableName).append(" (");
	    int fieldCount = insertValues.size(), index = 0;
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
	    	    PreparedStatement stmt = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS)) {

	    	    int parameterIndex = 1;
	    	    for (Object value : insertValues.values()) {
	    	        stmt.setObject(parameterIndex++, value);
	    	    }
	    	    System.out.println(stmt);
	    	    stmt.executeUpdate();

	    	    Long generatedKeysList = null;

	            // Check if rows were inserted and get the generated keys
	    	    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedKeysList = generatedKeys.getLong(1);
                    }
                }
	            return generatedKeysList;
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
	    	    System.out.println(stmt);
	    	    stmt.executeUpdate();
	    	}
	}
	
	//Update operation
	default void update(QueryRequest request) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ").append(request.getTableName());
        QueryHelper helper = new QueryHelper();
        
        List<String> whereConditions = request.getWhereConditions();
        Map<String, Object> updates = request.getUpdates();

        sql.append(" SET ");
        helper.appendUpdateValues(sql, updates);

        if (whereConditions != null && !whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            helper.appendConditions(sql, whereConditions, request.getWhereConditionsValues(),request.getWhereOperators(), request.getWhereLogicalOperators());
        }
        
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
        	helper.setParameters(stmt, updates, request.getWhereConditionsValues());
            System.out.println(stmt);
        	stmt.executeUpdate();
        }
    }

    //Select operation
    default List<Map<String, Object>> select(QueryRequest request) throws SQLException, Exception {
        StringBuilder sql = new StringBuilder("SELECT ");
        List<Map<String, Object>> list = null;
        QueryHelper helper = new QueryHelper();
        
        String tableName = request.getTableName();
        String joinTableName = request.getJoinTableName();

        Map<String, Object> tableField = YamlMapper.getTableField(tableName);
        if(joinTableName!=null)
        {
        	tableField.putAll(YamlMapper.getTableField(joinTableName));
        }
        
        if(request.getSelectAllColumns() == true) {
        	sql.append(" * ");
        }
        else {
        	helper.appendSelectColumns(sql, request.getSelectColumns());
        }
        sql.append(" FROM ").append(tableName);
        
        
        
        if (joinTableName != null) {
            sql.append(" JOIN ").append(joinTableName).append(" ON ");
            helper.appendJoinConditions(sql, tableName, joinTableName, request.getJoinConditions(), request.getJoinOperators(), request.getJoinLogicalOperators());
        }

        if (request.getWhereConditions() != null && !request.getWhereConditions().isEmpty()) {
        	sql.append(" WHERE ");
            helper.appendConditions(sql, request.getWhereConditions(), request.getWhereConditionsValues(),request.getWhereOperators(), request.getWhereLogicalOperators());
        }

        if (request.getOrderByColumns() != null && !request.getOrderByColumns().isEmpty()) {
            sql.append(" ORDER BY ");
            List<String> orderClauses = new ArrayList<>();
            
            for (int i = 0; i < request.getOrderByColumns().size(); i++) {
                String column = request.getOrderByColumns().get(i);
                String direction = request.getOrderDirections() != null && i < request.getOrderDirections().size()
                    ? request.getOrderDirections().get(i)
                    : "ASC"; // Default to ASC if no direction specified
                orderClauses.add(column + " " + direction);
            }
            sql.append(String.join(", ", orderClauses));
        }


        if (request.getLimit() != null) {
            sql.append(" LIMIT ").append(request.getLimit());
        }

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
        	helper.setParameters(stmt, null, request.getWhereConditionsValues());
            System.out.println(stmt);
        	ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
            	if(list==null)
            	{
            		list = new ArrayList<>();
            	}
            	
            	Map<String, Object> map = new HashMap<>();
            	
            	if(request.getSelectColumns()!=null) {
            		for(String columnName : request.getSelectColumns())
            		{
            			String fieldName = ((Map<String, String>) tableField.get(columnName)).get("pojoname");
            			map.put(fieldName, rs.getObject(columnName));
            		}
            		list.add(map);
            	}
            	else {
            		ResultSetMetaData metaData = rs.getMetaData();
            	    int columnCount = metaData.getColumnCount();
            	    for (int i = 1; i <= columnCount; i++) {
            	        String columnName = metaData.getColumnName(i);
            	        String fieldName = ((Map<String, String>) tableField.get(columnName)).get("pojoname");
            	        map.put(fieldName, rs.getObject(columnName));
            	    }
            	    list.add(map);
            	}
            }
            return list;
        }
        catch (Exception e) {
        	e.printStackTrace();
			throw new Exception("Failed getting the data.");
		}
        
    }
}