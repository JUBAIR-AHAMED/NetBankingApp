package com.netbanking.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryBuilder;
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
        
    	qb.join(joins)
    	.set(updateFields)
    	.where(whereConditions, request.getWhereOperators(), request.getWhereLogicalOperators());
        
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
	
	public void updateMany(List<QueryRequest> requests) throws SQLException {
	    if (requests == null || requests.isEmpty()) {
	        throw new IllegalArgumentException("Request list cannot be null or empty");
	    }

	    try (Connection connection = DBConnection.getConnection()) {
	        connection.setAutoCommit(false); 

	        try {
	            for (QueryRequest request : requests) {
	                QueryBuilder qb = new QueryBuilder();

	                qb.update(request.getTableName())
	                    .join(request.getJoinConditions())
	                    .set(request.getUpdateField())
	                    .where(request.getWhereConditions(), request.getWhereOperators(), request.getWhereLogicalOperators())
	                    .endQuery();

	                try (PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
	                    int count = 1;

	                    count = DBConnection.setValuesInPstm(stmt, request.getUpdateValue(), count);

	                    if (request.getWhereConditionsValues() != null && !request.getWhereConditionsValues().isEmpty()) {
	                        count = DBConnection.setValuesInPstm(stmt, request.getWhereConditionsValues(), count);
	                    }

	                    stmt.executeUpdate();
	                }
	            }

	            connection.commit();
	        } catch (SQLException e) {
	            connection.rollback();
	            throw new SQLException("Transaction failed. All changes rolled back.", e);
	        }
	    } catch (SQLException e) {
	        throw new SQLException("Database connection error or transaction failure.", e);
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
        
    	qb.from(tableName).join(request.getJoinConditions())
    		.where(request.getWhereConditions(), request.getWhereOperators(), request.getWhereLogicalOperators())
        	.order(request.getOrderByColumns(), request.getOrderDirections())
            .limit(request.getLimit());
        
        Map<String, String> tableField = new HashMap<String, String>(YamlMapper.getFieldToColumnMapByTableName(tableName));
        if(joins!=null) {
        	for(Join join:joins)
        	{
        		tableField.putAll(YamlMapper.getFieldToColumnMapByTableName(join.getTableName()));
        	}
        }
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public T selectOneGetObject(QueryRequest request, Class<?> clazz) throws SQLException, Exception {
		T pojoObject = (T) clazz.getDeclaredConstructor().newInstance();
        Map<String, Object> pojoMap = select(request).get(0);

        for (Map.Entry<String, Object> pojoEntry : pojoMap.entrySet()) {
            Object value = pojoEntry.getValue();
            String methodName = "set" + capitalizeFirstLetter(pojoEntry.getKey());
            String fieldName = pojoEntry.getKey();

            Field field = null;
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                continue;
            }

            Class<?> fieldType = field.getType();

            if (fieldType.isEnum()) {
                value = Enum.valueOf((Class<Enum>) fieldType, value.toString());
            }

            if (fieldType != null) {
                Method setMethod = clazz.getMethod(methodName, fieldType);
                setMethod.invoke(pojoObject, value);
            }
        }
        return pojoObject;
    }

    
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the original string if it's null or empty
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}