package com.netbanking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.util.DBConnection;

public class DaoImpl<T> implements Dao<T> {
	//Insert operation
	public Long insert(String tableName, Map<String, Object> insertValues) throws SQLException {
		QueryBuilder qb = new QueryBuilder();
	    qb.insert(tableName, insertValues.keySet());
	    String sqlQuery = qb.finish();
	    try (Connection connection = DBConnection.getConnection();
    	    PreparedStatement stmt = connection.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
	    	
    	    DBConnection.setValuesInPstm(stmt, insertValues.values(), 1);
    	    System.out.println(stmt);
    	    stmt.executeUpdate();

    	    Long generatedKeysList = null;
    	    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedKeysList = generatedKeys.getLong(1);
                }
            }
            return generatedKeysList;
    	} 
	}
	
	//Delete operation
	public void delete(String tableName, List<String> whereFields, List<Object> whereValues, List<String> whereOperators, List<String> logicalOperators) throws SQLException {
	    QueryBuilder qb = new QueryBuilder();
	    if(whereFields != null && !whereFields.isEmpty()) {
	    	qb.delete(tableName).where(whereFields, whereOperators, logicalOperators);
	    }
		
		String sqlQuery = qb.finish();
	    try (Connection connection = DBConnection.getConnection();
    	     PreparedStatement stmt = connection.prepareStatement(sqlQuery.toString())) {
	    	if(whereFields != null && !whereFields.isEmpty())
	    	{
	    		DBConnection.setValuesInPstm(stmt, whereValues, 1);
	    		stmt.executeUpdate();
	    	}
	    }
	}
	
	//Update operation
	public void update(QueryRequest request) throws SQLException {
        QueryBuilder qb = new QueryBuilder();
        qb.update(request.getTableName());
        List<String> whereConditions = request.getWhereConditions();
        List<Join> joins = request.getJoinConditions();
        List<String> updateFields = request.getUpdateField();
        
        if(joins!=null) {
        	qb.join(request.getJoinConditions());
        }
        if(updateFields!=null && !updateFields.isEmpty()) {
        	qb.set(request.getUpdateField());
        }
        if (whereConditions != null && !whereConditions.isEmpty()) {
            qb.where(whereConditions, request.getWhereOperators(), request.getWhereLogicalOperators());
        }
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
        	int count = 1;
        	count = DBConnection.setValuesInPstm(stmt, request.getUpdateValue(), count);
        	if(whereConditions != null && !whereConditions.isEmpty()) {
        		DBConnection.setValuesInPstm(stmt, request.getWhereConditionsValues(), count);
        	}
        	stmt.executeUpdate();
        }
    }

    //Select operation
    public List<Map<String, Object>> select(QueryRequest request) throws SQLException, Exception {
    	String tableName = request.getTableName();
    	QueryBuilder qb = new QueryBuilder();
        List<Join> joins = request.getJoinConditions();
    	if(request.getSelectAllColumns()) {
        	qb.select();
        } else {
        	qb.select(request.getSelectColumns());
        }
        qb.from(tableName);
        
        if(joins!=null) {
        	qb.join(request.getJoinConditions());
        }
        if(request.getWhereConditions() != null && !request.getWhereConditions().isEmpty()) {
        	qb.where(request.getWhereConditions(), request.getWhereOperators(), request.getWhereLogicalOperators());
        }
        if(request.getOrderByColumns() != null && !request.getOrderByColumns().isEmpty()) {
        	qb.order(request.getOrderByColumns(), request.getOrderDirections());
        }
        if (request.getLimit() != null) {
            qb.limit(request.getLimit());
        }
        
        Map<String, String> tableField = new HashMap<String, String>(YamlMapper.getFieldToColumnMapByTableName(tableName));
        if(joins!=null) {
        	for(Join join:joins)
        	{
        		tableField.putAll(YamlMapper.getFieldToColumnMapByTableName(join.getTableName()));
        	}
        }
        System.out.println(tableField);
        List<Map<String, Object>> list = new ArrayList<>();;
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
        	int count = 1;
        	DBConnection.setValuesInPstm(stmt, request.getWhereConditionsValues(), count);
        	System.out.println(stmt);
        	ResultSet rs = stmt.executeQuery();
        	// Preparing map for the purpose of returning the selected values from the database
            while(rs.next())
            {
            	Map<String, Object> map = new HashMap<>();
            	if(request.getSelectColumns()!=null) {
            		for(String columnName : request.getSelectColumns())
            		{
						String fieldName = tableField.get(columnName);
            			map.put(columnName, rs.getObject(fieldName));
            		}
            	}
            	else {
            	    for(Map.Entry<String, String> entry:tableField.entrySet()) {
            	    	String columnName = entry.getValue();
            	    	map.put(entry.getKey(), rs.getObject(columnName));
            	    }
            	}
            	list.add(map);
            }
            return list;
        }
        catch (Exception e) {
        	e.printStackTrace();
			throw new Exception("Failed getting the data.");
		}
    }
}