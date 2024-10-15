package com.netbanking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.netbanking.mapper.GenericMapper;
import com.netbanking.mapper.ReflectionMapper;
import com.netbanking.model.Model;
import com.netbanking.util.DBConnection;
import com.netbanking.util.TableHelper;

public interface Dao {

	default <T extends Model> void insert(T entity) throws SQLException {
		String tableName = TableHelper.getTableName(entity);
		GenericMapper<T> mapper = new ReflectionMapper<T>();

		try(Connection connection = DBConnection.getConnection();)
		{
		    // Map fields from entity
		    Map<String, Object> fields = mapper.toMap(entity);

		    // Check if fields map is empty
		    if (fields.isEmpty()) {
		        throw new SQLException("No fields found to insert for entity: " + entity);
		    }
		    
		    // Dynamically construct the SQL query
		    StringBuilder sql = new StringBuilder("INSERT INTO ");
		    sql.append(tableName).append(" (");
		    
		    // Add columns	
		    int index = 0;
		    int fieldSize = fields.size();
		    for (String column : fields.keySet()) {
		        sql.append(column);
		        if (index < fieldSize - 1) {
		            sql.append(", ");
		        }
		        index++;
		    }
		    sql.append(") VALUES (");
		    
		    // Add placeholders
		    index = 0;
		    for (int i = 0; i < fieldSize; i++) {
		        sql.append("?");
		        if (i < fieldSize - 1) {
		            sql.append(", ");
		        }
		    }
		    sql.append(")");
	
		    // Prepare the statement
		    try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
		        index = 0;
		        for (Object value : fields.values()) {
		            stmt.setObject(index + 1, value);
		            index++;
		        }
	
		        // Execute the insert
		        stmt.executeUpdate();
		        System.out.println("Insert successful for table: " + tableName);
		    }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
//	public <T extends Model> void update(T entity) throws SQLException {
//	    String tableName = TableHelper.getTableName(entity);
//
//	    try (Connection connection = DBConnection.getConnection()) {
//	        GenericMapper<T> mapper = new ReflectionMapper<>();
//	        Map<String, Object> fields = mapper.toMap(entity);
//
//	        StringBuilder sql = new StringBuilder("UPDATE ");
//	        sql.append(tableName).append(" SET ");
//
//	        int index = 0;
//	        int fieldSize = fields.size();
//	        for (String column : fields.keySet()) {
//	            if (!column.equals(entity.getIdField())) {
//	                sql.append(column).append(" = ?");
//	                if (index < fieldSize - 1) {
//	                    sql.append(", ");
//	                }
//	                index++;
//	            }
//	        }
//	        // Move the WHERE clause outside of the loop
//	        sql.append(" WHERE ").append(entity.getIdField()).append(" = ?");
//
//	        System.out.println("Update query: " + sql.toString());
//
//	        try (PreparedStatement updateStmt = connection.prepareStatement(sql.toString())) {
//	            index = 0;
//
//	            // Set the parameters for the columns being updated
//	            for (Map.Entry<String, Object> entry : fields.entrySet()) {
//	                if (!entity.getIdField().equals(entry.getKey())) {
//	                    updateStmt.setObject(index + 1, entry.getValue());
//	                    index++;
//	                }
//	            }
//
//	            // Set the parameter for the WHERE clause
//	            updateStmt.setObject(index + 1, entity.getId()); // Use your ID getter method
//	            updateStmt.executeUpdate();
//	        }
//	    }
//	}
//	
	default void update(String tableName,
					   Map<String, Object> updateValues, 
					   Map<String, Object> updateConditionValues, 
					   List<String> operators,
					   List<String> conditions,
					   String joinTableName,
					   Map<String, Object> joinTableUpdateValues,
					   Map<String, Object> joinTableUpdateConditionValues,
					   Map<String, String> onCondition) throws SQLException {

		StringBuilder sql = new StringBuilder("UPDATE ").append(tableName);
		
		int onMapSize = onCondition.size();
		int index = 1;
		
		if(joinTableName != null)
		{
			sql.append(" JOIN ").append(joinTableName).append(" ON ");
			for(Map.Entry<String, String> entry : onCondition.entrySet())
			{
				sql.append(tableName+"."+entry.getKey()).append(" = ").append(joinTableName+"."+entry.getKey());
				if(index < onMapSize)
				{
					sql.append(", ");
				}
				index++;
			}
		}		

		sql.append(" SET ");
		
		int updateMapSize = updateValues.size();
		int updateConditionMapSize = updateConditionValues.size();
		index=1;

		for(Map.Entry<String, Object> entry : updateValues.entrySet())
		{
			sql.append(entry.getKey()).append(" = ? ");
			if(index < updateMapSize)
			{
				sql.append(", ");
			}
			index++;
		}
		
		sql.append("WHERE ");
		index=1;
		
		for(Map.Entry<String, Object> entry : updateConditionValues.entrySet())
		{
			Object value = entry.getValue();
			if(value.getClass().isEnum())
			{
				value = ((Enum<?>) value).name();
			}
			sql.append(entry.getKey()).append(" ").append(operators.get(index-1)).append(" ? ");
			if(index < updateConditionMapSize)
			{
				sql.append(conditions.get(index-1)).append(" ");
			}
			index++;
		}
		
		System.out.println(sql);
		index=1;
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql.toString()))
		{
			for(Map.Entry<String, Object> entry : updateValues.entrySet())
			{
				Object value = entry.getValue();
				if(value.getClass().isEnum())
				{
					value = ((Enum<?>) value).name();
				}
				stmt.setObject(index, value);
				index++;
			}
			
			for(Map.Entry<String, Object> entry : updateConditionValues.entrySet())
			{
				Object value = entry.getValue();
				if(value.getClass().isEnum())
				{
					value = ((Enum<?>) value).name();
				}
				System.out.println(value);
				stmt.setObject(index, value);
				index++;
			}
			stmt.executeUpdate();
		}
	}
	
	default void delete(String tableName, Map<String, Object> deleteConditionValues, List<String> operators, List<String> conditions) throws SQLException
	{
		StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName);
		
		int updateConditionMapSize = deleteConditionValues.size();
		int index=1;
		
		sql.append(" WHERE ");
		index=1;
		
		for(Map.Entry<String, Object> entry : deleteConditionValues.entrySet())
		{
			Object value = entry.getValue();
			if(value.getClass().isEnum())
			{
				value = ((Enum<?>) value).name();
			}
			sql.append(entry.getKey()).append(" ").append(operators.get(index-1)).append(" ? ");
			if(index < updateConditionMapSize)
			{
				sql.append(conditions.get(index-1)).append(" ");
			}
			index++;
		}
		
		System.out.println(sql);
		index=1;
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql.toString()))
		{			
			for(Map.Entry<String, Object> entry : deleteConditionValues.entrySet())
			{
				Object value = entry.getValue();
				if(value.getClass().isEnum())
				{
					value = ((Enum<?>) value).name();
				}
				System.out.println(value);
				stmt.setObject(index, value);
				index++;
			}
			System.out.println(stmt);
			stmt.executeUpdate();
			System.out.println("done");
		}
	}
	
	public List<List<Object>> select(String tableName, List<String> selectFields, Map<String, Object> updateConditionValues, List<String> operators, List<String> conditions) throws SQLException
	{
		StringBuilder sql = new StringBuilder("SELECT ");
		List<List<Object>> resultList = new ArrayList<>();
		int selectFieldSize = selectFields.size();
		int updateConditionMapSize = updateConditionValues.size();
		int index=1;

		for(String fields : selectFields)
		{
			sql.append(fields);
			if(index < selectFieldSize)
			{
				sql.append(", ");
			}
			index++;
		}
		
		sql.append(" from ")
			.append(tableName)
			.append(" WHERE ");
		
		index=1;
		
		for(Map.Entry<String, Object> entry : updateConditionValues.entrySet())
		{
			Object value = entry.getValue();
			if(value.getClass().isEnum())
			{
				value = ((Enum<?>) value).name();
			}
			sql.append(entry.getKey()).append(" ").append(operators.get(index-1)).append(" ? ");
			if(index < updateConditionMapSize)
			{
				sql.append(conditions.get(index-1)).append(" ");
			}
			index++;
		}
		
		System.out.println(sql);
		index=1;
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql.toString()))
		{			
			for(Map.Entry<String, Object> entry : updateConditionValues.entrySet())
			{
				Object value = entry.getValue();
				if(value.getClass().isEnum())
				{
					value = ((Enum<?>) value).name();
				}
				stmt.setObject(index, value);
				index++;
			}
			System.out.println(stmt);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
			{
				List<Object> tempList = new ArrayList<>();
				for(String fields : selectFields)
				{
					tempList.add(rs.getObject(fields));
				}
				resultList.add(tempList);
			}
			return resultList;
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
	}
}