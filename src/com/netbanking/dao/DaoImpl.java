package com.netbanking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.netbanking.daoObject.Condition;
import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryBuilder;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Where;
import com.netbanking.mapper.PojoValueMapper;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.util.DBConnection;

public class DaoImpl<T> implements Dao<T> {
	//Insert operation
	public Long insertHandler(T object) throws Exception {
		if(object==null) {
			throw new Exception("Object is null.");
		}		
		Class<?> clazz = object.getClass();
		Class<?> superClass = clazz.getSuperclass();
		
		//Getting Table Names
		List<String> objectNames = new ArrayList<String>();
		objectNames.add(clazz.getSimpleName());
		while(superClass!=null && !superClass.getSimpleName().equals("Object")) {
			objectNames.add(0, superClass.getSimpleName());
			superClass = superClass.getSuperclass();
		}
		Map<String, Object> pojoValuesMap = null;
		try {
			pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		Long refrenceKey = null;
		for(String objectName : objectNames) {
			String tableName = YamlMapper.getTableName(objectName);
			Map<String, Object> objectData = YamlMapper.getObjectData(objectName);
			@SuppressWarnings("unchecked")
			Map<String, Object> objectFieldData = (Map<String, Object>) objectData.get("table_field_name");
			Map<String, Object> insertValues = new HashMap<String, Object>();
			String autoinc = null;
			if(objectData.containsKey("autoincrement_field"))
			{
				autoinc = (String) objectData.get("autoincrement_field");
			}
			for(Map.Entry<String, Object> tempMap : objectFieldData.entrySet())
			{
				String key = tempMap.getKey();
				String keyNameInTable  = (String)objectFieldData.get(key);
				if(key.equals(autoinc))
				{
					continue;
				} 
				if(objectData.containsKey("refrenceingKey") && objectData.get("refrenceingKey").equals(key)) {
					insertValues.put(keyNameInTable, refrenceKey);
					continue;
				} 
				Object value = pojoValuesMap.get(key);
				insertValues.put(keyNameInTable, value);
			}
			try {
				Long tempRef = insert(tableName, insertValues);
				if(refrenceKey==null)
				{
					refrenceKey =  tempRef;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception("Failed Inserting");
			}
		}
		return refrenceKey;
	}
	
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
	public void update(T object, QueryRequest request) throws Exception {
		Map<String, Object> pojoValuesMap = null;
		try {
			pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
        List<String> whereConditions = request.getWhereConditions();
		List<String> updateFields = new ArrayList<String>(pojoValuesMap.keySet());
        List<Object> updateValues = new ArrayList<Object>(pojoValuesMap.values());
		List<Where> whereConditionsType = request.getWhereConditionsType();
		String tableName = request.getTableName();	    		
	    
	    if(updateFields==null || updateFields.isEmpty()) {
	    	return;
	    }
	    
	    convertFields(tableName, updateFields);

	    List<Object> whereConditionsValues = new ArrayList<>();
	    if(whereConditions!=null && !whereConditions.isEmpty()) {
	    	convertFields(tableName, whereConditions);
	    	whereConditionsValues = request.getWhereConditionsValues();
	    } else if(whereConditionsType!=null) {
			whereConditions = new ArrayList<String>();
			for(Where entity: whereConditionsType) {
				String whereTableName = entity.getTable();
				String field = entity.getField();
				Object value = entity.getValue();
				field = convertField(whereTableName, field);
				StringBuilder sb = new StringBuilder(whereTableName).append(".").append(field);
				whereConditions.add(sb.toString());
				whereConditionsValues.add(value);
			}
		}

        QueryBuilder qb = new QueryBuilder();
        qb.update(request.getTableName());
        
    	qb.set(updateFields)
    	.where(whereConditions, request.getWhereOperators(), request.getWhereLogicalOperators());
        
    	try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
        	int count = 1;
        	count = DBConnection.setValuesInPstm(stmt, updateValues, count);
        	if(whereConditions != null && !whereConditions.isEmpty()) {
        		DBConnection.setValuesInPstm(stmt, whereConditionsValues, count);
        	}
        	stmt.executeUpdate();
        }
    }

    //Select operation
    public List<Map<String, Object>> select(QueryRequest request) throws SQLException, Exception {
    	String tableName = request.getTableName();
		List<String> whereConditions=request.getWhereConditions();
		List<Where> whereConditionsType = request.getWhereConditionsType();
		List<Join> joins = request.getJoinConditions();
		List<Condition> selects = request.getSelects();
		List<String> selectList = new ArrayList<String>();
		
		if(selects!=null && !selects.isEmpty()) {
			for(Condition select:selects) {
				String table = select.getTable();
				String field = select.getField();
				field = convertField(table, field);
				selectList.add(field);
			}
		}
		
		if(joins!=null&&!joins.isEmpty()) {
			for(Join join:joins) {
				int joinConditionLength = join.getLeftTable().size();
				List<String> leftTable=join.getLeftTable(), leftColumn=join.getLeftColumn(), rightTable=join.getRightTable(), rightColumn=join.getRightColumn();
				for(int i=0;i<joinConditionLength;i++) {
					String leftTableName = leftTable.get(i), leftColumnName = leftColumn.remove(i), rightTableName = rightTable.get(i), rightColumnName = rightColumn.remove(i);
					leftColumn.add(i, convertField(leftTableName, leftColumnName));
					rightColumn.add(i, convertField(rightTableName, rightColumnName));
				}
			}
		}
		
		List<Object> whereConditionsValues = new ArrayList<>();
	    if(whereConditions!=null && !whereConditions.isEmpty()) {
	    	convertFields(tableName, whereConditions);
	    	whereConditionsValues = request.getWhereConditionsValues();
	    } else if(whereConditionsType!=null) {
			whereConditions = new ArrayList<String>();
			for(Where entity: whereConditionsType) {
				String whereTableName = entity.getTable();
				String field = entity.getField();
				Object value = entity.getValue();
				field = convertField(whereTableName, field);
				StringBuilder sb = new StringBuilder(whereTableName).append(".").append(field);
				whereConditions.add(sb.toString());
				whereConditionsValues.add(value);
			}
		}
		    	
		//sbdbadc
    	QueryBuilder qb = new QueryBuilder();
//        List<Join> joins = request.getJoinConditions();
    	if(request.getSelectAllColumns()) {
        	qb.select();
        } else {
        	qb.select(selectList);
        }
        
    	qb.from(tableName).join(joins)
    		.where(whereConditions, request.getWhereOperators(), request.getWhereLogicalOperators())
        	.order(request.getOrderByColumns(), request.getOrderDirections())
            .limit(request.getLimit());
        
        Map<String, String> tableField = new HashMap<String, String>(YamlMapper.getFieldToColumnMapByTableName(tableName));
        if(joins!=null) {
        	for(Join join:joins)
        	{
        		tableField.putAll(YamlMapper.getFieldToColumnMapByTableName(join.getTableName()));
        	}
        }
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
        	int count = 1;
        	DBConnection.setValuesInPstm(stmt, whereConditionsValues, count);
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
    
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the original string if it's null or empty
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    
    public void convertFields(String tableName, List<String> fields) {
		Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(tableName);
		for(int i=0;i<fields.size();i++) {
			String fieldName = fields.remove(i);
	        if (fieldToColumnMap.containsKey(fieldName)) {
	        	if (fieldToColumnMap.containsKey(fieldName)) {
		            fields.add(i, fieldToColumnMap.get(fieldName));
		        }
	        }
		}
	}
	
	public String convertField(String tableName, String field) throws Exception {
		Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(tableName);
		if(fieldToColumnMap==null) {
			throw new Exception("Table name is invalid");
		}
		String newField = fieldToColumnMap.get(field);
		if(newField==null) {
			throw new Exception("Field name is invalid.");
		}
		return newField;
	}
}